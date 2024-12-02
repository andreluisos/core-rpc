package org.jnvim.corerpc.message;

/**
 * Represents a generator for ids for messages. It can generate them in many different ways, but
 * important thing is that they are unique.
 */
public interface MessageIdGenerator {

    /**
     * Get next id. It must not be an id that was already used
     *
     * @return next id
     */
    int nextId();
}
