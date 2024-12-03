package client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import message.Message;
import message.MessageIdGenerator;
import message.NotificationMessage;
import message.RequestMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ExtendWith(MockitoExtension.class)
public class PackStreamTest {

    @Mock RpcListener rpcListener;

    @Mock RpcSender rpcSender;

    @Mock MessageIdGenerator messageIdGenerator;

    @InjectMocks PackStream packStream;

    @Mock InputStream inputStream;

    @Mock OutputStream outputStream;

    private RpcConnection connection;

    private ArgumentCaptor<RpcListener.RequestCallback> packStreamRequestCallback;
    private ArgumentCaptor<RpcListener.NotificationCallback> packStreamNotificationCallback;

    @BeforeEach
    public void setUp() {
        // No global stubbings to avoid UnnecessaryStubbingException
    }

    @Test
    public void testAttach() throws IOException {
        // Given
        connection =
                new RpcConnection() {
                    @Override
                    public InputStream getIncomingStream() {
                        return inputStream;
                    }

                    @Override
                    public OutputStream getOutgoingStream() {
                        return outputStream;
                    }

                    @Override
                    public void close() throws IOException {}
                };
        // No stubbing needed as methods are mocked
        // When attach is called
        packStream.attach(connection);
        // Then
        verify(rpcListener).listenForRequests(any());
        verify(rpcListener).listenForNotifications(any());
        verify(rpcListener).start(inputStream);
        verify(rpcSender).attach(outputStream);
    }

    @Test
    public void testSend() throws IOException {
        // When send is called
        Message message = () -> null;
        packStream.send(message);
        // Then
        verify(rpcSender).send(message);
    }

    @Test
    public void testSendRequest() throws IOException {
        // Given a proper message id generator
        given(messageIdGenerator.nextId()).willReturn(25);
        // When send is called
        var message = new RequestMessage.Builder("test");
        packStream.send(message);
        // Then
        var argumentCaptor = ArgumentCaptor.forClass(RequestMessage.class);
        verify(rpcSender).send(argumentCaptor.capture());
        assertEquals("test", argumentCaptor.getValue().getMethod());
        // And id for the message should be generated
        verify(messageIdGenerator).nextId();
        // And put into the message
        assertEquals(25, argumentCaptor.getValue().getId());
    }

    @Test
    public void testSendRequestWithCallback() throws IOException {
        // Given a proper message id generator
        given(messageIdGenerator.nextId()).willReturn(25);
        // And callback
        var responseCallback = Mockito.mock(RpcListener.ResponseCallback.class);
        // When send is called
        var message = new RequestMessage.Builder("test");
        packStream.send(message, responseCallback);
        // Then
        var argumentCaptor = ArgumentCaptor.forClass(RequestMessage.class);
        verify(rpcSender).send(argumentCaptor.capture());
        assertEquals("test", argumentCaptor.getValue().getMethod());
        // And id for the message should be generated
        verify(messageIdGenerator).nextId();
        // And put into the message
        assertEquals(25, argumentCaptor.getValue().getId());
        // RPC Listener should be used too
        verify(rpcListener).listenForResponse(25, responseCallback);
    }

    @Test
    public void testRequestCallback() throws IOException {
        // Given a proper rpc listener and attached pack stream
        prepareListeners();
        connection =
                new RpcConnection() {
                    @Override
                    public InputStream getIncomingStream() {
                        return inputStream;
                    }

                    @Override
                    public OutputStream getOutgoingStream() {
                        return outputStream;
                    }

                    @Override
                    public void close() throws IOException {}
                };
        packStream.attach(connection);
        // When request callback is added
        var firstCallback = Mockito.mock(RpcListener.RequestCallback.class);
        packStream.addRequestCallback(firstCallback);
        // It should receive requests
        var msg1 = new RequestMessage.Builder("test").build();
        packStreamRequestCallback.getValue().requestReceived(msg1);
        verify(firstCallback).requestReceived(msg1);
        // Multiple callbacks should be supported too
        var secondCallback = Mockito.mock(RpcListener.RequestCallback.class);
        packStream.addRequestCallback(secondCallback);
        // Both should receive messages
        var msg2 = new RequestMessage.Builder("test2").build();
        packStreamRequestCallback.getValue().requestReceived(msg2);
        verify(firstCallback).requestReceived(msg2);
        verify(secondCallback).requestReceived(msg2);
        // Removing should be supported
        packStream.removeRequestCallback(firstCallback);
        // Only second should receive message now
        var msg3 = new RequestMessage.Builder("test3").build();
        packStreamRequestCallback.getValue().requestReceived(msg3);
        verify(firstCallback, never()).requestReceived(msg3);
        verify(secondCallback).requestReceived(msg3);
        // Multiple removals
        packStream.removeRequestCallback(secondCallback);
        // None should receive message now
        var msg4 = new RequestMessage.Builder("test4").build();
        packStreamRequestCallback.getValue().requestReceived(msg4);
        verify(firstCallback, never()).requestReceived(msg4);
        verify(secondCallback, never()).requestReceived(msg4);
    }

    @Test
    public void testNotificationCallback() throws IOException {
        // Given a proper rpc listener and attached pack stream
        prepareListeners();
        connection =
                new RpcConnection() {
                    @Override
                    public InputStream getIncomingStream() {
                        return inputStream;
                    }

                    @Override
                    public OutputStream getOutgoingStream() {
                        return outputStream;
                    }

                    @Override
                    public void close() throws IOException {}
                };
        packStream.attach(connection);
        // When notification callback is added
        var firstCallback = Mockito.mock(RpcListener.NotificationCallback.class);
        packStream.addNotificationCallback(firstCallback);
        // It should receive notifications
        var msg1 = new NotificationMessage.Builder("test").build();
        packStreamNotificationCallback.getValue().notificationReceived(msg1);
        verify(firstCallback).notificationReceived(msg1);
        // Multiple callbacks should be supported too
        var secondCallback = Mockito.mock(RpcListener.NotificationCallback.class);
        packStream.addNotificationCallback(secondCallback);
        // Both should receive messages
        var msg2 = new NotificationMessage.Builder("test2").build();
        packStreamNotificationCallback.getValue().notificationReceived(msg2);
        verify(firstCallback).notificationReceived(msg2);
        verify(secondCallback).notificationReceived(msg2);
        // Removing should be supported
        packStream.removeNotificationCallback(firstCallback);
        // Only second should receive message now
        var msg3 = new NotificationMessage.Builder("test3").build();
        packStreamNotificationCallback.getValue().notificationReceived(msg3);
        verify(firstCallback, never()).notificationReceived(msg3);
        verify(secondCallback).notificationReceived(msg3);
        // Multiple removals
        packStream.removeNotificationCallback(secondCallback);
        // None should receive message now
        var msg4 = new NotificationMessage.Builder("test4").build();
        packStreamNotificationCallback.getValue().notificationReceived(msg4);
        verify(firstCallback, never()).notificationReceived(msg4);
        verify(secondCallback, never()).notificationReceived(msg4);
    }

    @Test
    public void noNullRpcListener() {
        // When null rpc listener is passed, constructor should throw an exception
        assertThrows(
                NullPointerException.class,
                () -> {
                    new PackStream(rpcSender, null);
                },
                "Expected constructor to throw NullPointerException when rpcListener is null");
        assertThrows(
                NullPointerException.class,
                () -> {
                    new PackStream(rpcSender, null, messageIdGenerator);
                },
                "Expected constructor to throw NullPointerException when rpcListener is null");
    }

    @Test
    public void noNullRpcSender() {
        // When null rpc sender is passed, constructor should throw an exception
        assertThrows(
                NullPointerException.class,
                () -> {
                    new PackStream(null, rpcListener);
                },
                "Expected constructor to throw NullPointerException when rpcSender is null");
        assertThrows(
                NullPointerException.class,
                () -> {
                    new PackStream(null, rpcListener, messageIdGenerator);
                },
                "Expected constructor to throw NullPointerException when rpcSender is null");
    }

    @Test
    public void noNullMessageIdGenerator() {
        // When null message id generator is passed, constructor should throw an exception
        assertThrows(
                NullPointerException.class,
                () -> {
                    new PackStream(rpcSender, rpcListener, null);
                },
                "Expected constructor to throw NullPointerException when messageIdGenerator is"
                        + " null");
    }

    private void prepareListeners() {
        packStreamNotificationCallback =
                ArgumentCaptor.forClass(RpcListener.NotificationCallback.class);
        doNothing()
                .when(rpcListener)
                .listenForNotifications(packStreamNotificationCallback.capture());
        packStreamRequestCallback = ArgumentCaptor.forClass(RpcListener.RequestCallback.class);
        doNothing().when(rpcListener).listenForRequests(packStreamRequestCallback.capture());
    }
}
