package org.jnvim.corerpc.message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple implementation of {@link MessageIdGenerator} creating IDs using an {@link AtomicInteger}
 */
public final class SequentialMessageIdGenerator implements MessageIdGenerator {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public int nextId() {
        return atomicInteger.incrementAndGet();
    }
}
