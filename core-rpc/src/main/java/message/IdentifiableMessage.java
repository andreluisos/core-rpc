package org.jnvim.corerpc.message;

/**
 * Defines a {@link Message} which has an ID ID has to be stored, since these messages usually
 * receive a response (unless they are response themselves)
 */
public interface IdentifiableMessage extends Message {

    /**
     * Get ID of the message
     *
     * @return integer representing ID of the message
     */
    int getId();
}
