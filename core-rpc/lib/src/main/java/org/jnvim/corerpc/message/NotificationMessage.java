package org.jnvim.corerpc.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;

/**
 * Defines a notification Notifications should not be considered high priority and are not expected
 * to block execution They should be handled when possible
 *
 * <p>Format is defined as: * type as Integer * name (event name) as String * arguments as Array
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"type", "name", "arguments"})
@JsonDeserialize(builder = NotificationMessage.Builder.class)
public final class NotificationMessage implements Message {

    private final String name;
    private final ArrayList<Object> arguments;

    private NotificationMessage(Builder builder) {
        this(builder.name, builder.arguments);
    }

    /**
     * Creates a new {@link NotificationMessage} This should not be used outside this library, since
     * it represents incoming notifications
     *
     * @param name notification name
     * @param arguments arguments of notification
     */
    public NotificationMessage(String name, ArrayList<Object> arguments) {
        this.name = name;
        this.arguments = new ArrayList<>(arguments);
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("arguments")
    public ArrayList<?> getArguments() {
        return arguments;
    }

    @Override
    @JsonProperty("type")
    public MessageType getType() {
        return MessageType.NOTIFICATION;
    }

    /**
     * Builder for {@link NotificationMessage} just for convention Since all other {@link Message}
     * implementations contain builder
     *
     * <p>This should not be used outside of library, since notifications are usually only incoming
     * messages
     */
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({"type", "name", "arguments"})
    @JsonPOJOBuilder
    public static class Builder {
        private final String name;
        private final ArrayList<Object> arguments;

        /**
         * Creates a new {@link NotificationMessage.Builder} with just a name. Arguments are set to
         * an empty array. They may be added additionally
         *
         * @param name notification name
         */
        public Builder(String name) {
            this(name, new ArrayList<>());
        }

        /**
         * Creates a new {@link NotificationMessage.Builder} with name and arguments More arguments
         * may be added
         *
         * @param name notification name
         * @param arguments arguments of notification
         */
        @JsonCreator
        public Builder(
                @JsonProperty("name") String name,
                @JsonProperty("arguments") ArrayList<?> arguments) {
            this.name = name;
            this.arguments = new ArrayList<>(arguments);
        }

        /**
         * Adds all arguments provided
         *
         * @param arguments argument list to add
         */
        public Builder addArguments(ArrayList<?> arguments) {
            this.arguments.addAll(arguments);
            return this;
        }

        /**
         * Adds a single argument
         *
         * @param argument argument to add
         */
        public Builder addArgument(Object argument) {
            this.arguments.add(argument);
            return this;
        }

        /**
         * Creates a new {@link NotificationMessage} using arguments added to this instance
         *
         * @return a new {@link NotificationMessage}. Multiple calls will create different
         *     instances.
         */
        public NotificationMessage build() {
            return new NotificationMessage(this);
        }
    }

    @Override
    public String toString() {
        return "NotificationMessage{" + "name='" + name + '\'' + ", arguments=" + arguments + '}';
    }
}
