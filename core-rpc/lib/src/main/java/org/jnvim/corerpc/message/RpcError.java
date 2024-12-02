package org.jnvim.corerpc.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Class defining an error used in RPC communication It is not an error in the communication itself,
 * rather an error that is sent by applications communicating to indicate an error (bad request, bad
 * payload, etc.)
 */
@JsonFormat(shape = JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"id", "message"})
public final class RpcError {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    public static final class Type {
        public static final Type EXCEPTION = new Type(0);
        public static final Type VALIDATION = new Type(1);

        private final int id;

        public Type(int id) {
            this.id = id;
        }

        @JsonCreator
        public static Type fromInt(int value) {
            switch (value) {
                case 0:
                    return EXCEPTION;
                case 1:
                    return VALIDATION;
                default:
                    return new Type(value);
            }
        }

        @JsonValue
        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Type{" + "value=" + id + '}';
        }
    }

    private final Type type;
    private final String message;

    public RpcError(int type, String message) {
        this.type = Type.fromInt(type);
        this.message = message;
    }

    @JsonCreator
    public RpcError(@JsonProperty("id") Type type, @JsonProperty("message") String message) {
        this.type = type;
        this.message = message;
    }

    @JsonIgnore
    public Type getType() {
        return type;
    }

    @JsonProperty("id")
    public int getId() {
        return type.getId();
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public static RpcError exception(String message) {
        return new RpcError(Type.EXCEPTION, message);
    }

    public static RpcError validation(String message) {
        return new RpcError(Type.VALIDATION, message);
    }

    public static RpcError other(int typeId, String message) {
        return new RpcError(Type.fromInt(typeId), message);
    }

    public static RpcError other(Type type, String message) {
        return new RpcError(type, message);
    }

    private String typeToString() {
        if (type.id == Type.EXCEPTION.id) {
            return "Exception";
        } else if (type.id == Type.VALIDATION.id) {
            return "Validation";
        } else {
            return type.toString();
        }
    }

    @Override
    public String toString() {
        return "RpcError{" + "type=" + typeToString() + ", message='" + message + '\'' + '}';
    }
}
