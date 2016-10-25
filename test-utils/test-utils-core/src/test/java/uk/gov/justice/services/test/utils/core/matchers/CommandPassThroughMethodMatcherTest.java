package uk.gov.justice.services.test.utils.core.matchers;

import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;
import static uk.gov.justice.services.test.utils.core.matchers.CommandPassThroughMethodMatcher.passThroughMethod;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;

import org.junit.Test;

public class CommandPassThroughMethodMatcherTest {

    @Test
    public void shouldMatchAPassThroughCommandMethod() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testA"));
    }

    @Test
    public void shouldMatchAPassThroughCommandMethodThatHasHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testA").thatHandles("testA"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotCallSender() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testB"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodCallsSenderMoreThanOnce() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testC"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotHaveJsonEnvelopeArgument() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testD"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotSendCommand() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testE"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchIfMethodDoesNotExist() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testF"));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchAPassThroughCommandMethodThatDoesNotHaveHandlesAnnotation() throws Exception {
        assertThat(ValidCommandApi.class, passThroughMethod("testG").thatHandles("testG"));
    }

    @ServiceComponent(COMMAND_API)
    public static class ValidCommandApi {

        @Inject
        Sender sender;

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
    }
}