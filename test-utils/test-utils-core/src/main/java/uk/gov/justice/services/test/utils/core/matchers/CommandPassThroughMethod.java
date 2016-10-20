package uk.gov.justice.services.test.utils.core.matchers;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import uk.gov.justice.services.core.sender.Sender;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.test.utils.core.helper.ServiceComponents;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class CommandPassThroughMethod extends TypeSafeDiagnosingMatcher<Method> {

    public static CommandPassThroughMethod passThroughMethod() {
        return new CommandPassThroughMethod();
    }

    @Override
    protected boolean matchesSafely(final Method method, final Description description) {
        try {
            final Sender sender = mock(Sender.class, format("%s.sender.send", method.getName()));
            final JsonEnvelope command = mock(JsonEnvelope.class);
            final Class<?> handlerClass = method.getDeclaringClass();
            final Object handlerInstance = handlerClass.newInstance();

            final Field senderField = ServiceComponents.findField(handlerClass, Sender.class);
            senderField.setAccessible(true);
            senderField.set(handlerInstance, sender);

            method.invoke(handlerInstance, command);

            verify(sender).send(command);

            return true;

        } catch (Exception ex) {
            description.appendText("Method ")
                    .appendValue(method.getName())
                    .appendText(" of class ")
                    .appendValue(method.getDeclaringClass())
                    .appendText(" is not a command pass through method");
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Is a command pass through method");
    }
}
