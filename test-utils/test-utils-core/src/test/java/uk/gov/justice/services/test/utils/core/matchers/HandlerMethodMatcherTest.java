package uk.gov.justice.services.test.utils.core.matchers;

import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.dispatcher.Requester;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;

import org.junit.Test;

public class HandlerMethodMatcherTest {

    @Test
    public void shouldMatchMethod() throws Exception {
        assertThat(ValidCommandApi.class, method("testA"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchMissingMethod() throws Exception {
        assertThat(ValidCommandApi.class, method("missingMethodName"));
    }

    @Test
    public void shouldMatchMethodWithHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, method("testA").thatHandles("testA"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchMethodWithWrongHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, method("testA").thatHandles("wrongActionName"));
    }

    @Test
    public void shouldMatchAPassThroughCommandMethodThatHasHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, method("testA").thatHandles("testA").withSenderPassThrough());
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotCallSender() throws Exception {
        assertThat(ValidCommandApi.class, method("testB").thatHandles("testB").withSenderPassThrough());
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodCallsSenderMoreThanOnce() throws Exception {
        assertThat(ValidCommandApi.class, method("testC").thatHandles("testC").withSenderPassThrough());
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotHaveJsonEnvelopeArgument() throws Exception {
        assertThat(ValidCommandApi.class, method("testD"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotSendCommand() throws Exception {
        assertThat(ValidCommandApi.class, method("testE").thatHandles("testE").withSenderPassThrough());
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchAPassThroughCommandMethodThatDoesNotHaveHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, method("testG").thatHandles("testG"));
    }

    @Test
    public void shouldMatchAPassThroughRequesterMethod() throws Exception {
        assertThat(ValidCommandApi.class, method("testF").thatHandles("testF").withRequesterPassThrough());
    }

    @ServiceComponent(COMMAND_API)
    public static class ValidCommandApi {

        @Inject
        Sender sender;

        @Inject
        Requester requester;

        @Handles("testA")
        public void testA(final JsonEnvelope command) {
            sender.send(command);
        }

        @Handles("testB")
        public void testB(final JsonEnvelope command) {
        }

        @Handles("testC")
        public void testC(final JsonEnvelope command) {
            sender.send(command);
            sender.send(command);
        }

        @Handles("testD")
        public void testD() {
            sender.send(null);
        }

        @Handles("testE")
        public void testE(final JsonEnvelope command) {
            sender.send(null);
        }

        public void testG(final JsonEnvelope command) {
            sender.send(command);
        }

        @Handles("testF")
        public JsonEnvelope testF(final JsonEnvelope query) {
            return requester.request(query);
        }
    }
}
