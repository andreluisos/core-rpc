package client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import message.MessageType;
import message.NotificationMessage;
import message.RequestMessage;
import message.ResponseMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
public class BackgroundRpcListenerTest {

    @Mock ExecutorService executorService;

    @Mock ObjectMapper objectMapper;

    @Mock ObjectReader objectReader;

    @Mock InputStream inputStream;

    @InjectMocks BackgroundRpcListener backgroundRpcListener;

    @Test
    public void testStart() throws IOException {
        // Given a proper executor service and object mapper
        prepareSequentialExecutorService();
        given(objectMapper.reader()).willReturn(objectReader);
        given(objectReader.readTree(inputStream))
                .willReturn(JsonNodeFactory.instance.arrayNode(), (JsonNode) null);

        // When start is called, nothing special happens
        backgroundRpcListener.start(inputStream);
    }

    @Test
    public void testRequestListener() throws IOException {
        // Given a proper executor service and object mapper
        prepareSequentialExecutorService();
        given(objectMapper.reader()).willReturn(objectReader);
        var requestNode = prepareRequestNode();
        var requestMessage = new RequestMessage.Builder("test").build();
        given(objectMapper.treeToValue(any(), eq(RequestMessage.class))).willReturn(requestMessage);
        given(objectReader.readTree(inputStream)).willReturn(requestNode, (JsonNode) null);
        var requestCallback = Mockito.mock(RpcListener.RequestCallback.class);

        backgroundRpcListener.listenForRequests(requestCallback);
        backgroundRpcListener.start(inputStream);

        verify(requestCallback).requestReceived(requestMessage);
    }

    @Test
    public void testRequestListenerWithoutStart() {
        // No stubbings required since no methods are called

        var requestCallback = Mockito.mock(RpcListener.RequestCallback.class);

        backgroundRpcListener.listenForRequests(requestCallback);

        verify(requestCallback, never()).requestReceived(any());
    }

    @Test
    public void testResponseListener() throws IOException {
        // Given a proper executor service and object mapper
        prepareSequentialExecutorService();
        given(objectMapper.reader()).willReturn(objectReader);
        var responseNode = prepareResponseNode();
        var responseMessage = new ResponseMessage.Builder("test").build();
        given(objectMapper.treeToValue(any(), eq(ResponseMessage.class)))
                .willReturn(responseMessage);
        given(objectReader.readTree(inputStream)).willReturn(responseNode, (JsonNode) null);
        var responseCallback = Mockito.mock(RpcListener.ResponseCallback.class);

        backgroundRpcListener.listenForResponse(responseMessage.getId(), responseCallback);
        backgroundRpcListener.start(inputStream);

        verify(responseCallback).responseReceived(responseMessage.getId(), responseMessage);
    }

    @Test
    public void testResponseListenerWithoutStart() {
        // No stubbings required since no methods are called

        var responseCallback = Mockito.mock(RpcListener.ResponseCallback.class);

        backgroundRpcListener.listenForResponse(1, responseCallback);

        verify(responseCallback, never()).responseReceived(anyInt(), any());
    }

    @Test
    public void testNotificationListener() throws IOException {
        // Given a proper executor service and object mapper
        prepareSequentialExecutorService();
        given(objectMapper.reader()).willReturn(objectReader);
        var notificationNode = prepareNotificationNode();
        var notificationMessage = new NotificationMessage.Builder("test").build();
        given(objectMapper.treeToValue(any(), eq(NotificationMessage.class)))
                .willReturn(notificationMessage);
        given(objectReader.readTree(inputStream)).willReturn(notificationNode, (JsonNode) null);
        var notificationCallback = Mockito.mock(RpcListener.NotificationCallback.class);

        backgroundRpcListener.listenForNotifications(notificationCallback);
        backgroundRpcListener.start(inputStream);

        verify(notificationCallback).notificationReceived(notificationMessage);
    }

    @Test
    public void testNotificationListenerWithoutStart() {
        // No stubbings required since no methods are called

        var notificationCallback = Mockito.mock(RpcListener.NotificationCallback.class);

        backgroundRpcListener.listenForNotifications(notificationCallback);

        verify(notificationCallback, never()).notificationReceived(any());
    }

    @Test
    @Timeout(2)
    public void testStopping() throws IOException, InterruptedException {
        // Given a proper executor service and object mapper
        var multiLatch = new MultiLatch(10);
        executorService = Executors.newSingleThreadScheduledExecutor();
        backgroundRpcListener = new BackgroundRpcListener(executorService, objectMapper);
        given(objectMapper.reader()).willReturn(objectReader);
        var notificationNode = prepareNotificationNode();
        var notificationMessage = new NotificationMessage.Builder("test").build();
        given(objectMapper.treeToValue(any(), eq(NotificationMessage.class)))
                .willReturn(notificationMessage);
        given(objectReader.readTree(inputStream))
                .willAnswer(
                        invocation -> {
                            multiLatch.await();
                            return notificationNode;
                        });

        var notificationCallback = Mockito.mock(RpcListener.NotificationCallback.class);
        doAnswer(
                        invocation -> {
                            System.out.println(invocation.getArguments()[0]);
                            return null;
                        })
                .when(notificationCallback)
                .notificationReceived(any());

        backgroundRpcListener.listenForNotifications(notificationCallback);
        backgroundRpcListener.start(inputStream);

        multiLatch.countDown();
        verify(notificationCallback, timeout(100)).notificationReceived(notificationMessage);
        multiLatch.countDown();
        verify(notificationCallback, timeout(100).times(2))
                .notificationReceived(notificationMessage);

        // After listener is stopped, no more notifications should arrive
        backgroundRpcListener.stop();
        multiLatch.countDown();
        multiLatch.countDown();
        multiLatch.countDown();
        multiLatch.countDown();
        verifyNoMoreInteractions(notificationCallback);
    }

    @Test
    public void exceptionOnStartIsThrown() throws IOException {
        // Given an error in reading from stream
        prepareSequentialExecutorService();
        given(objectMapper.reader()).willThrow(new IOException());

        // When listener is started, app should crash
        assertThrows(
                RuntimeException.class,
                () -> {
                    backgroundRpcListener.start(inputStream);
                });
    }

    @Test
    public void noNullExecutorService() {
        // When null executor service is passed to constructor, it throws exception
        assertThrows(
                NullPointerException.class,
                () -> {
                    new BackgroundRpcListener(null, objectMapper);
                });
    }

    @Test
    public void noNullObjectMapper() {
        // When null object mapper is passed to constructor, it throws exception
        assertThrows(
                NullPointerException.class,
                () -> {
                    new BackgroundRpcListener(executorService, null);
                });
    }

    private ArrayNode prepareRequestNode() {
        var arrayNode = JsonNodeFactory.instance.arrayNode(4);
        arrayNode.add(MessageType.REQUEST.asInt());
        arrayNode.add(MessageType.REQUEST.asInt());
        arrayNode.add(MessageType.REQUEST.asInt());
        arrayNode.add(MessageType.REQUEST.asInt());
        return arrayNode;
    }

    private ArrayNode prepareResponseNode() {
        var arrayNode = JsonNodeFactory.instance.arrayNode(4);
        arrayNode.add(MessageType.RESPONSE.asInt());
        arrayNode.add(MessageType.RESPONSE.asInt());
        arrayNode.add(MessageType.RESPONSE.asInt());
        arrayNode.add(MessageType.RESPONSE.asInt());
        return arrayNode;
    }

    private ArrayNode prepareNotificationNode() {
        var arrayNode = JsonNodeFactory.instance.arrayNode(3);
        arrayNode.add(MessageType.NOTIFICATION.asInt());
        arrayNode.add(MessageType.NOTIFICATION.asInt());
        arrayNode.add(MessageType.NOTIFICATION.asInt());
        return arrayNode;
    }

    private void prepareSequentialExecutorService() {
        doAnswer(
                        invocation -> {
                            ((Runnable) invocation.getArguments()[0]).run();
                            return null;
                        })
                .when(executorService)
                .submit(any(Runnable.class));
    }
}
