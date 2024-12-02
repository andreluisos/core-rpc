package org.jnvim.corerpc.message;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Defines types used in RPC communication
 *
 * <p>Contains int value used in RPC communication which represents the type
 */
public enum MessageType {
    REQUEST(0),
    RESPONSE(1),
    NOTIFICATION(2);

    private final int value;

    MessageType(int intValue) {
        this.value = intValue;
    }

    /**
     * Transforms passed integer into {@link MessageType}
     *
     * @param value a valid integer representing {@link MessageType} (it should be inside bounds [0
     *     - 2])
     * @return Corresponding instance of {@link MessageType}
     * @throws IllegalArgumentException if value is not inside bounds
     */
    public static MessageType fromInt(int value) {
        for (var messageType : values()) {
            if (value == messageType.value) {
                return messageType;
            }
        }
        throw new IllegalArgumentException(
                String.format("MessageType with int value of (%d) does not exist!", value));
    }

    /**
     * Gets integer representation of this {@link MessageType}
     *
     * @return Corresponding integer value
     */
    @JsonValue
    public int asInt() {
        return value;
    }
}
