package client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@ExtendWith(MockitoExtension.class)
public class TcpSocketRpcConnectionTest {

    @Mock Socket socket;

    @Mock InputStream inputStream;

    @Mock OutputStream outputStream;

    @InjectMocks TcpSocketRpcConnection tcpSocketRpcConnection;

    @Test
    public void testIncomingStream() throws IOException {
        given(socket.getInputStream()).willReturn(inputStream);
        assertEquals(inputStream, tcpSocketRpcConnection.getIncomingStream());
    }

    @Test
    public void testOutgoingStream() throws IOException {
        given(socket.getOutputStream()).willReturn(outputStream);
        assertEquals(outputStream, tcpSocketRpcConnection.getOutgoingStream());
    }

    @Test
    public void testExceptionInOpeningIncomingStream() throws IOException {
        given(socket.getInputStream()).willThrow(new IOException());
        // When incoming stream is requested, it should throw a RuntimeException
        assertThrows(
                RuntimeException.class,
                () -> {
                    tcpSocketRpcConnection.getIncomingStream();
                });
    }

    @Test
    public void testExceptionInOpeningOutgoingStream() throws IOException {
        given(socket.getOutputStream()).willThrow(new IOException());
        // When outgoing stream is requested, it should throw a RuntimeException
        assertThrows(
                RuntimeException.class,
                () -> {
                    tcpSocketRpcConnection.getOutgoingStream();
                });
    }

    @Test
    public void testClose() throws IOException {
        tcpSocketRpcConnection.close();
        verify(socket).close();
    }
}
