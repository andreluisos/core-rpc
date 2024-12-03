package client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@ExtendWith(MockitoExtension.class)
public class ProcessRpcConnectionTest {

    @Mock Process process;

    @Mock InputStream inputStream;

    @Mock OutputStream outputStream;

    ProcessRpcConnection processRpcConnection;

    @BeforeEach
    public void setUp() {
        processRpcConnection = new ProcessRpcConnection(process);
    }

    @Test
    public void testIncomingStream() throws IOException {
        // Given
        given(process.getInputStream()).willReturn(inputStream);
        // When & Then
        assertEquals(inputStream, processRpcConnection.getIncomingStream());
    }

    @Test
    public void testOutgoingStream() throws IOException {
        // Given
        given(process.getOutputStream()).willReturn(outputStream);
        // When & Then
        assertEquals(outputStream, processRpcConnection.getOutgoingStream());
    }

    @Test
    public void testClose() throws IOException {
        // Test without auto-closing
        var connection = new ProcessRpcConnection(process);
        connection.close();
        verify(process, never()).destroy();
        // Test with auto-closing
        var closingConnection = new ProcessRpcConnection(process, true);
        closingConnection.close();
        verify(process).destroy();
        // Test with try-with-resources
        var newProcess = Mockito.mock(Process.class);
        try (var autoClosedConnection = new ProcessRpcConnection(newProcess, true)) {
            // Optionally stub methods if needed
            // given(newProcess.getInputStream()).willReturn(inputStream);
            autoClosedConnection.getIncomingStream();
        }
        verify(newProcess).destroy();
    }
}
