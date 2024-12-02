package org.jnvim.corerpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementation of {@link RpcConnection} using stdio streams
 *
 * <p>This is mostly useful to communicate with nvim process that spawned this process, usually as a
 * remote plugin
 *
 * <p>Example:
 *
 * <pre>{@code
 * RpcConnection neovimConnection = new StdIoRpcConnection();
 *
 * // This can now be used for communication
 * rpcStreamer.attach(neovimConnection);
 * rpcStreamer.send(message); // sends a message to the parent neovim instance
 *
 * }</pre>
 */
public final class StdIoRpcConnection implements RpcConnection {
    public static final Logger log = LoggerFactory.getLogger(StdIoRpcConnection.class);

    /** Creates a new {@link StdIoRpcConnection} based on system stdio */
    public StdIoRpcConnection() {}

    @Override
    public InputStream getIncomingStream() {
        return System.in;
    }

    @Override
    public OutputStream getOutgoingStream() {
        return System.out;
    }

    @Override
    public void close() throws IOException {
        log.info("Closing stdio connection.");
    }

    @Override
    public String toString() {
        return "StdIoRpcConnection{}";
    }
}
