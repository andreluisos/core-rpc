package org.jnvim.corerpc.message;

/** Defines a single message in RPC communication */
public interface Message {

    /**
     * Returns the type of message
     *
     * <p>Used to determine type of the message without checking for actual class
     *
     * @return {@link MessageType} enum corresponding to the type of the message
     */
    MessageType getType();
}
