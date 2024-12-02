package client;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a bi-directional RPC connection (it may represent any connection, due to it being very
 * generic)
 */
public interface RpcConnection extends Closeable {

    /**
     * Incoming data stream (coming from other participant)
     *
     * @return {@link InputStream} with incoming data
     */
    InputStream getIncomingStream();

    /**
     * Outgoing data stream (going to other participant)
     *
     * @return {@link OutputStream} for outgoing data
     */
    OutputStream getOutgoingStream();
}
