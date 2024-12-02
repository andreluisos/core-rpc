package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Implementation of {@link RpcConnection} providing streams of a {@link Process}
 *
 * <p>This allows a connection to another process and communication with it via Rpc It is a very
 * simple implementation which can optionally kill the process once connection is closed
 *
 * <p>Example:
 *
 * <pre>{@code
 * ProcessBuilder pb = new ProcessBuilder("nvim", "--embed");
 * Process neovim = pb.start();
 *
 * RpcConnection neovimConnection = new ProcessRpcConnection(neovim, true);
 *
 * // This can now be used for communication
 * rpcStreamer.attach(neovimConnection);
 * rpcStreamer.send(message); // sends a message to the embedded neovim instance
 *
 * }</pre>
 */
public final class ProcessRpcConnection implements RpcConnection {
    public static final Logger log = LoggerFactory.getLogger(ProcessRpcConnection.class);

    private Process process;
    private boolean killProcessOnClose;

    /**
     * Creates a new {@link ProcessRpcConnection} based on a {@link Process}'s input and output
     * streams By default does not kill process when connection is closed
     *
     * @param process instance of {@link Process} to connect to
     * @throws NullPointerException if process is null
     */
    public ProcessRpcConnection(Process process) {
        this(process, false);
    }

    /**
     * Creates a new {@link ProcessRpcConnection} based on a {@link Process}'s input and output
     * streams
     *
     * @param process instance of {@link Process} to connect to
     * @param killProcessOnClose true if process should be destroyed when connection is closed
     * @throws NullPointerException if process is null
     */
    public ProcessRpcConnection(Process process, boolean killProcessOnClose) {
        Objects.requireNonNull(
                process, "process is required to properly implement a RpcConnection");
        this.process = process;
        this.killProcessOnClose = killProcessOnClose;
    }

    /**
     * Provides input stream of underlying process
     *
     * @return {@link InputStream} of the underlying process
     */
    @Override
    public InputStream getIncomingStream() {
        return process.getInputStream();
    }

    /**
     * Provides ouput stream of underlying process
     *
     * @return {@link OutputStream} of the underlying process
     */
    @Override
    public OutputStream getOutgoingStream() {
        return process.getOutputStream();
    }

    /**
     * Closes connection and optionally kills the underlying process if {@link #killProcessOnClose}
     * is true If {@link #killProcessOnClose} is true, communication is no longer possible
     * Otherwise, communication may proceed, because it is a no-op in that case
     *
     * @throws IOException - never
     */
    @Override
    public void close() throws IOException {
        log.info("Closing process connection. Killing process = {}", killProcessOnClose);
        if (killProcessOnClose) {
            process.destroy();
        }
    }

    @Override
    public String toString() {
        return "ProcessRpcConnection{"
                + "process="
                + process
                + ", killProcessOnClose="
                + killProcessOnClose
                + '}';
    }
}
