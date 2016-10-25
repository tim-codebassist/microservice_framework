package uk.gov.justice.services.test.utils.core.matchers;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.PassThroughType.REQUESTER;
import static uk.gov.justice.services.test.utils.core.matchers.HandlerMethodMatcher.PassThroughType.SENDER;
import static uk.gov.justice.services.test.utils.core.matchers.MethodHandlesAnnotationMatcher.methodThatHandles;

import uk.gov.justice.services.core.dispatcher.Requester;
import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.helper.ServiceComponents;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HandlerMethodMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    private static final int ONCE = 1;
    private String methodName;
    private Optional<String> action = Optional.empty();
    private Optional<PassThroughType> passThroughType = Optional.empty();

    public HandlerMethodMatcher(final String methodName) {
        this.methodName = methodName;
    }

    public static HandlerMethodMatcher method(final String methodName) {
        return new HandlerMethodMatcher(methodName);
    }

    public HandlerMethodMatcher thatHandles(final String action) {
        this.action = Optional.of(action);
        return this;
    }

    public HandlerMethodMatcher withSenderPassThrough() {
        this.passThroughType = Optional.of(SENDER);
        return this;
    }

    public HandlerMethodMatcher withRequesterPassThrough() {
        this.passThroughType = Optional.of(REQUESTER);
        return this;
    }

    @Override
    protected boolean matchesSafely(final Class<?> handlerClass, final Description description) {

        final Method method;
        try {
            method = handlerClass.getMethod(methodName, JsonEnvelope.class);
        } catch (final Exception ex) {
            description
                    .appendText("Class ")
                    .appendValue(handlerClass)
                    .appendText("has no method ")
                    .appendValue(methodName);
            return false;
        }

        if (action.isPresent() && !methodThatHandles(action.get()).matches(method)) {
            methodThatHandles(action.get()).describeMismatch(method, description);
            return false;
        }

        if (passThroughType.isPresent()) {
            switch (passThroughType.get()) {
                case SENDER:
                    return isSenderPassthrough(handlerClass, description);
                case REQUESTER:
                    return isRequesterPassthrough(handlerClass, description);
            }
        }

        return true;
    }

    private boolean isSenderPassthrough(final Class<?> handlerClass, final Description description) {
        try {
            final Method method = handlerClass.getMethod(methodName, JsonEnvelope.class);
            final Sender sender = mock(Sender.class, format("%s.sender.send", method.getName()));
            final JsonEnvelope command = mock(JsonEnvelope.class);
            final Object handlerInstance = handlerClass.newInstance();

            final Field senderField = ServiceComponents.findField(handlerClass, Sender.class);
            senderField.setAccessible(true);
            senderField.set(handlerInstance, sender);

            method.invoke(handlerInstance, command);
            verify(sender, times(ONCE)).send(command);

            return true;

        } catch (final Exception ex) {
            description.appendText("Method ")
                    .appendValue(methodName)
                    .appendText(" of class ")
                    .appendValue(handlerClass)
                    .appendText(" is not a sender pass-through method");
            return false;
        }
    }

    private boolean isRequesterPassthrough(final Class<?> handlerClass, final Description description) {
        try {
            final Method method = handlerClass.getMethod(methodName, JsonEnvelope.class);
            final Requester requester = mock(Requester.class, format("%s.requester.request", method.getName()));
            final JsonEnvelope query = mock(JsonEnvelope.class);
            final JsonEnvelope response = mock(JsonEnvelope.class);
            final Object handlerInstance = handlerClass.newInstance();

            final Field requesterField = ServiceComponents.findField(handlerClass, Requester.class);
            requesterField.setAccessible(true);
            requesterField.set(handlerInstance, requester);

            when(requester.request(query)).thenReturn(response);

            assertThat(method.invoke(handlerInstance, query), is(response));
            verify(requester, times(ONCE)).request(query);

            return true;

        } catch (final Exception ex) {
            description.appendText("Method ")
                    .appendValue(methodName)
                    .appendText(" of class ")
                    .appendValue(handlerClass)
                    .appendText(" is not a requester pass-through method");
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Is a command pass through method");
    }

    enum PassThroughType {
        SENDER, REQUESTER
    }
}
