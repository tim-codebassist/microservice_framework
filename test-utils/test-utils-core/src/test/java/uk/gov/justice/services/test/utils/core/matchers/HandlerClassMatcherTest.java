package uk.gov.justice.services.test.utils.core.matchers;

import static org.junit.Assert.assertThat;
import static uk.gov.justice.services.core.annotation.Component.COMMAND_API;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.method;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerClassMatcher.isHandlerClass;

import uk.gov.justice.services.core.annotation.Handles;
import uk.gov.justice.services.core.annotation.ServiceComponent;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;

import org.junit.Test;

public class HandlerClassMatcherTest {

    @Test
    public void shouldMatchPassThroughMethodOfServiceClass() throws Exception {
        assertThat(ValidCommandApi.class, isHandlerClass().with(method("testA").thatHandles("testA")));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchWhenNoHandlerMethod() throws Exception {
        assertThat(InValidNoHandlerMethod.class, isHandlerClass().with(method("testA").thatHandles("testA")));
    }

    @Test(expected = AssertionError.class)
    public void shouldNotMatchWhenNoHandlesAnnotation() throws Exception {
        assertThat(InValidNoHandlesAnnotation.class, isHandlerClass()
                .with(method("testA").thatHandles("testA")));
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
            sender.send(command);
        }
    }

    @ServiceComponent(COMMAND_API)
    public static class InValidNoHandlerMethod {

        @Inject
        Sender sender;
    }

    @ServiceComponent(COMMAND_API)
    public static class InValidNoHandlesAnnotation {

        @Inject
        Sender sender;

        public void testA(final JsonEnvelope command) {
            sender.send(command);
        }
    }

    public static class NoServiceComponentAnnotation {

        @Inject
        Sender sender;

        @Handles("testA")
        public void testA(final JsonEnvelope command) {
            sender.send(command);
        }
    }

    @ServiceComponent(COMMAND_API)
    public static class NoSenderField {

        @Handles("testA")
        public void testA(final JsonEnvelope command) {
        }
    }

    @ServiceComponent(COMMAND_API)
    public static class NoPassThroughInvocation {
        @Inject
        private Sender sender;

        @Handles("testA")
        public void testA(final JsonEnvelope command) {

        }
    }
}
