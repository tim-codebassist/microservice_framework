package uk.gov.justice.services.test.utils.core.matchers;

import static uk.gov.justice.services.test.utils.core.helper.ServiceComponents.isNotServiceComponent;
import static uk.gov.justice.services.test.utils.core.matchers.CommandPassThroughMethod.passThroughMethod;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ServiceClassPassThroughMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    private final Map<String, MethodHandlesMatcher> methodsToMatch = new HashMap<>();

    public static ServiceClassPassThroughMatcher isCommandPassThrough() {
        return new ServiceClassPassThroughMatcher();
    }

    @Override
    protected boolean matchesSafely(final Class<?> handlerClass, final Description description) {

        if (isNotServiceComponent(handlerClass)) {
            description.appendValue(handlerClass.getName())
                    .appendText(" is not annotated as a Service Component");
            return false;
        }

        if (methodsToMatch.entrySet().isEmpty()) {
            description.appendValue(handlerClass.getName())
                    .appendText(" has no methods to match");
            return false;
        }

        for (Map.Entry<String, MethodHandlesMatcher> methodEntry : methodsToMatch.entrySet()) {
            final String methodName = methodEntry.getKey();
            final MethodHandlesMatcher handlesMatcher = methodEntry.getValue();

            try {
                final Method method = handlerClass.getMethod(methodName, JsonEnvelope.class);

                if (handlesMatcher.matchesSafely(method, description)) {
                    passThroughMethod().matchesSafely(method, description);
                } else {
                    return false;
                }

            } catch (NoSuchMethodException ex) {
                description.appendText("Method ")
                        .appendValue(methodName)
                        .appendText(" does not exist in ")
                        .appendValue(handlerClass.getSimpleName());
                return false;
            }

        }

        return true;
    }

    @Override
    public void describeTo(final Description description) {

    }

    public ServiceClassPassThroughMatcher hasMethod(final String methodName) {
        this.methodsToMatch.put(methodName, null);
        return this;
    }

    public ServiceClassPassThroughMatcher hasMethod(final String methodName, final MethodHandlesMatcher methodHandlesMatcher) {
        this.methodsToMatch.put(methodName, methodHandlesMatcher);
        return this;
    }
}
