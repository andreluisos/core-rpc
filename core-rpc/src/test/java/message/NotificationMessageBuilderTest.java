package message;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class NotificationMessageBuilderTest {

    @Test
    public void testFullConstructor() {
        // Given a name and arguments
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new NotificationMessage.Builder("test", arguments);
        // When builder builds response
        var notificationMessage = builder.build();
        // It should contain these objects and should have NOTIFICATION type
        assertEquals("test", notificationMessage.getName());
        assertEquals(arguments, notificationMessage.getArguments());
        assertEquals(MessageType.NOTIFICATION, notificationMessage.getType());
        // New calls should also work right
        assertEquals("test", builder.build().getName());
        assertEquals(arguments, builder.build().getArguments());
        assertEquals(MessageType.NOTIFICATION, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testNewInstanceEveryTime() {
        // Given a name and arguments
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new NotificationMessage.Builder("test", arguments);
        // When builder builds multiple requests
        var notificationMessage = builder.build();
        // They should not be same
        assertNotEquals(notificationMessage, builder.build());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testNameConstructor() {
        // Given a builder with just a name
        var builder = new NotificationMessage.Builder("test");
        // When build is called
        // Result should contain given method
        assertEquals("test", builder.build().getName());
        // And arguments should be empty
        assertEquals(0, builder.build().getArguments().size());
        // Type should still be NOTIFICATION
        assertEquals(MessageType.NOTIFICATION, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testAddArgument() {
        // Given a builder with some defaults
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new NotificationMessage.Builder("test", arguments);
        var notificationMessage = builder.build();
        assertEquals(arguments, notificationMessage.getArguments());
        // When add argument is called, it is added to the new list
        builder.addArgument("argTwo");
        assertEquals(2, builder.build().getArguments().size());
        // But not to the already built object
        assertEquals(1, notificationMessage.getArguments().size());
        // Everything else is the same
        assertEquals("test", builder.build().getName());
        assertEquals(MessageType.NOTIFICATION, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testAddArguments() {
        // Given a builder with some defaults
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new NotificationMessage.Builder("test", arguments);
        var notificationMessage = builder.build();
        assertEquals(arguments, notificationMessage.getArguments());
        // When add argument is called, it is added to the new list
        var extraArgs = new ArrayList<String>();
        extraArgs.add("argTwo");
        builder.addArguments(extraArgs);
        var twoArgsMessage = builder.build();
        assertEquals(2, twoArgsMessage.getArguments().size());
        // And not affecting any old objects
        extraArgs.add("argThree");
        builder.addArguments(
                extraArgs); // In this case, two extra args are added (argTwo and argThree)
        assertEquals(4, builder.build().getArguments().size());
        assertEquals(2, twoArgsMessage.getArguments().size());
        assertEquals(1, notificationMessage.getArguments().size());
        // Everything else is the same
        assertEquals("test", builder.build().getName());
        assertEquals(MessageType.NOTIFICATION, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }
}
