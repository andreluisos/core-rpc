package message;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class RequestMessageBuilderTest {

    @Test
    public void testFullConstructor() {
        // Given a method and arguments
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new RequestMessage.Builder("test", arguments);
        // When builder builds response
        var requestMessage = builder.build();
        // It should contain these objects and should have REQUEST type
        assertEquals("test", requestMessage.getMethod());
        assertEquals(arguments, requestMessage.getArguments());
        assertEquals(MessageType.REQUEST, requestMessage.getType());
        // New calls should also work right
        assertEquals("test", builder.build().getMethod());
        assertEquals(arguments, builder.build().getArguments());
        assertEquals(MessageType.REQUEST, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testNewInstanceEveryTime() {
        // Given a method and arguments
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new RequestMessage.Builder("test", arguments);
        // When builder builds multiple requests
        var requestMessage = builder.build();
        // They should not be same
        assertNotEquals(requestMessage, builder.build());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testMethodConstructor() {
        // Given a builder with just a method
        var builder = new RequestMessage.Builder("test");
        // When build is called
        // Result should contain given method
        assertEquals("test", builder.build().getMethod());
        // And arguments should be empty
        assertEquals(0, builder.build().getArguments().size());
        // Type should still be REQUEST
        assertEquals(MessageType.REQUEST, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testWithId() {
        // Given a builder with some defaults
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new RequestMessage.Builder("test", arguments);
        assertEquals(0, builder.build().getId());
        // When withId is called, new id is used
        builder.withId(5);
        assertEquals(5, builder.build().getId());
        // Everything else is the same
        assertEquals("test", builder.build().getMethod());
        assertEquals(arguments, builder.build().getArguments());
        assertEquals(MessageType.REQUEST, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testAddArgument() {
        // Given a builder with some defaults
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new RequestMessage.Builder("test", arguments);
        var requestMessage = builder.build();
        assertEquals(arguments, requestMessage.getArguments());
        // When add argument is called, it is added to the new list
        builder.addArgument("argTwo");
        assertEquals(2, builder.build().getArguments().size());
        // But not to the already built object
        assertEquals(1, requestMessage.getArguments().size());
        // Everything else is the same
        assertEquals("test", builder.build().getMethod());
        assertEquals(MessageType.REQUEST, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }

    @Test
    public void testAddArguments() {
        // Given a builder with some defaults
        var arguments = new ArrayList<String>();
        arguments.add("argOne");
        var builder = new RequestMessage.Builder("test", arguments);
        var requestMessage = builder.build();
        assertEquals(arguments, requestMessage.getArguments());
        // When add argument is called, it is added to the new list
        var extraArgs = new ArrayList<String>();
        extraArgs.add("argTwo");
        builder.addArguments(extraArgs);
        var twoArgsMessage = builder.build();
        assertEquals(2, twoArgsMessage.getArguments().size());
        // And not affecting any old objects
        extraArgs.add("argThree");
        builder.addArguments(
                extraArgs); // in this case two extra args are added (argTwo and argThree)
        assertEquals(4, builder.build().getArguments().size());
        assertEquals(2, twoArgsMessage.getArguments().size());
        // Everything else is the same
        assertEquals("test", builder.build().getMethod());
        assertEquals(MessageType.REQUEST, builder.build().getType());
        // To string doesn't crash
        assertDoesNotThrow(() -> builder.build().toString());
    }
}
