package client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;

import message.Message;
import message.RequestMessage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@ExtendWith(MockitoExtension.class)
public class RpcClientTest {

    @Mock RpcStreamer rpcStreamer;

    @InjectMocks RpcClient rpcClient;

    @Test
    public void delegatesToUnderlyingRpcStreamer() throws IOException {
        // Given a rpc client depending on a rpc streamer
        // All operations should be delegated to rpc streamer
        validateDelegates(rpcClient, rpcStreamer);
    }

    @Test
    public void testDefaultFactories() {
        // Create should create new instances
        var rpc1 = RpcClient.createDefaultAsyncInstance();
        var rpc2 = RpcClient.createDefaultAsyncInstance();
        assertNotEquals(rpc1, rpc2);
        // Get should return same instances
        var rpc3 = RpcClient.getDefaultAsyncInstance();
        var rpc4 = RpcClient.getDefaultAsyncInstance();
        assertEquals(rpc3, rpc4);
        // And it is different from the created ones
        assertNotEquals(rpc1, rpc3);
        assertNotEquals(rpc2, rpc3);
        var rpc5 = RpcClient.createDefaultAsyncInstance();
        assertNotEquals(rpc5, rpc4);
    }

    @Test
    public void testDefaultBuilder() {
        // Just build straight away
        var rpc1 = new RpcClient.Builder().build();
        assertNotNull(rpc1);
    }

    @Test
    public void testRpcStreamerBuilder() throws IOException {
        // When custom streamer is passed, it should be used
        var rpc1Streamer = Mockito.mock(RpcStreamer.class);
        var rpc1 = new RpcClient.Builder().withRpcStreamer(rpc1Streamer).build();
        validateDelegates(rpc1, rpc1Streamer);
    }

    @Test
    public void testObjectMapperBuilder() {
        // Use custom mapper / executor service
        var customObjectMapper = Mockito.mock(ObjectMapper.class);
        var executorService = Mockito.mock(ExecutorService.class);
        var rpc1 =
                new RpcClient.Builder()
                        .withObjectMapper(customObjectMapper)
                        .withExecutorService(executorService)
                        .build();
        assertNotNull(rpc1);
    }

    @Test
    public void testCustomRpcComponentsBuilder() {
        // Use custom mapper / executor service
        var customObjectMapper = Mockito.mock(ObjectMapper.class);
        var executorService = Mockito.mock(ExecutorService.class);
        var rpcSender = Mockito.mock(RpcSender.class);
        var rpcListener = Mockito.mock(RpcListener.class);
        // Building with just one custom component
        // Rpc Sender
        // Sender can be changed later
        var rpc1 =
                new RpcClient.Builder()
                        .withRpcSender(rpcSender)
                        .withObjectMapper(customObjectMapper)
                        .withExecutorService(executorService)
                        .withRpcSender(rpcSender)
                        .build();
        assertNotNull(rpc1);
        var rpc2 = new RpcClient.Builder().withRpcSender(rpcSender).build();
        assertNotNull(rpc2);
        // Rpc Listener
        // Listener can be changed later
        var rpc3 =
                new RpcClient.Builder()
                        .withRpcListener(rpcListener)
                        .withObjectMapper(customObjectMapper)
                        .withExecutorService(executorService)
                        .withRpcListener(rpcListener)
                        .build();
        assertNotNull(rpc3);
        var rpc4 = new RpcClient.Builder().withRpcListener(rpcListener).build();
        assertNotNull(rpc4);
        // With both
        var rpc5 =
                new RpcClient.Builder()
                        .withRpcListener(rpcListener)
                        .withRpcSender(rpcSender)
                        .build();
        assertNotNull(rpc5);
        var rpc6 =
                new RpcClient.Builder()
                        .withRpcSender(rpcSender)
                        .withRpcListener(rpcListener)
                        .build();
        assertNotNull(rpc6);
        // Components can be changed later
        var rpc7 =
                new RpcClient.Builder()
                        .withRpcSenderAndListener(rpcSender, rpcListener)
                        .withRpcSender(rpcSender)
                        .withRpcListener(rpcListener)
                        .build();
        assertNotNull(rpc7);
    }

    private void validateDelegates(RpcClient rpcClient, RpcStreamer rpcStreamer)
            throws IOException {
        var rpcConnection = Mockito.mock(RpcConnection.class);
        rpcClient.attach(rpcConnection);
        verify(rpcStreamer).attach(rpcConnection);
        Message message = () -> null;
        rpcClient.send(message);
        verify(rpcStreamer).send(message);
        var msgBuilder = new RequestMessage.Builder("test");
        rpcClient.send(msgBuilder);
        verify(rpcStreamer).send(msgBuilder);
        var responseCallback = Mockito.mock(RpcListener.ResponseCallback.class);
        rpcClient.send(msgBuilder, responseCallback);
        verify(rpcStreamer).send(msgBuilder, responseCallback);
        var requestCallback = Mockito.mock(RpcListener.RequestCallback.class);
        var notificationCallback = Mockito.mock(RpcListener.NotificationCallback.class);
        rpcClient.addRequestCallback(requestCallback);
        rpcClient.removeRequestCallback(requestCallback);
        rpcClient.addNotificationCallback(notificationCallback);
        rpcClient.removeNotificationCallback(notificationCallback);
        verify(rpcStreamer).addRequestCallback(requestCallback);
        verify(rpcStreamer).removeRequestCallback(requestCallback);
        verify(rpcStreamer).addNotificationCallback(notificationCallback);
        verify(rpcStreamer).removeNotificationCallback(notificationCallback);
    }
}
