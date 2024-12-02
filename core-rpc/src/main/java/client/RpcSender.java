package client;

import message.Message;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a RPC communication sender (writer) It should provide interface for sending messages
 * Message sending should occur on a separate thread
 */
public interface RpcSender {
    /**
     * Sends a message to attached {@link OutputStream} Implementations need to implement it
     * according to interface (requiring attachment prior to communication)
     *
     * @param message message to send
     * @throws IllegalStateException if current instance is not attached to a {@link OutputStream}
     * @throws IOException if issues arise in communication or serialization
     */
    void send(Message message) throws IOException;

    /**
     * Attaches this {@link RpcSender} to a {@link OutputStream} That {@link OutputStream} can (and
     * should) then be used to communicate (for sending data)
     *
     * @param outputStream {@link OutputStream} to write to
     */
    void attach(OutputStream outputStream);

    /** Stops the sender */
    void stop();
}
