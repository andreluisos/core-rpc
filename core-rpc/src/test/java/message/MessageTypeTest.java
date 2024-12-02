package message;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class MessageTypeTest {

    @Test
    public void fromIntTest() {
        // 0 should be request
        assertEquals(MessageType.REQUEST, MessageType.fromInt(0));
        // 1 should be response
        assertEquals(MessageType.RESPONSE, MessageType.fromInt(1));
        // 2 should be notification
        assertEquals(MessageType.NOTIFICATION, MessageType.fromInt(2));
    }

    @Test
    public void fromInvalidIntTest() {
        // when an invalid int is used it should throw an exception
        assertThrows(IllegalArgumentException.class, () -> MessageType.fromInt(27));
    }

    @Test
    public void asIntTest() {
        // 0 should be request
        assertEquals(0, MessageType.REQUEST.asInt());
        // 1 should be response
        assertEquals(1, MessageType.RESPONSE.asInt());
        // 2 should be notification
        assertEquals(2, MessageType.NOTIFICATION.asInt());
    }
}
