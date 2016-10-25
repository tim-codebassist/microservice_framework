package uk.gov.justice.services.test.utils.core.matchers;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.services.test.utils.core.matchers.MethodHandlesMatcher.methodThatHandles;

import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.helper.ServiceComponents;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class CommandPassThroughMethodMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    private static final int ONCE = 1;
    private String methodName;
    private Optional<String> annotatedValue = Optional.empty();

    public CommandPassThroughMethodMatcher(final String methodName) {
        this.methodName = methodName;
    }

    public static CommandPassThroughMethodMatcher passThroughMethod(final String methodName) {
        return new CommandPassThroughMethodMatcher(methodName);
    }

    public CommandPassThroughMethodMatcher thatHandles(final String annotatedValue) {
        this.annotatedValue = Optional.of(annotatedValue);
        return this;
    }

    @Override
    protected boolean matchesSafely(final Class<?> handlerClass, final Description description) {
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

            if (annotatedValue.isPresent() && !methodThatHandles(annotatedValue.get()).matches(method)) {
                methodThatHandles(annotatedValue.get()).describeMismatch(method, description);
                return false;
            }

            return true;

        } catch (Exception ex) {
            description.appendText("Method ")
                    .appendValue(methodName)
                    .appendText(" of class ")
                    .appendValue(handlerClass)
                    .appendText(" is not a command pass through method");
            return false;
        }
    }


    @Override
    public void describeTo(final Description description) {
        description.appendText("Is a command pass through method");
    }
}
