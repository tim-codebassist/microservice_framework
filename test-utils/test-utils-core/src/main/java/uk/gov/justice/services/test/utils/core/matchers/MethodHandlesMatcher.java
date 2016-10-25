package uk.gov.justice.services.test.utils.core.matchers;

import uk.gov.justice.services.core.annotation.Handles;

import java.lang.reflect.Method;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class MethodHandlesMatcher extends TypeSafeDiagnosingMatcher<Method> {

    private final String handles;

    public MethodHandlesMatcher(final String handles) {
        this.handles = handles;
    }

    public static MethodHandlesMatcher methodThatHandles(final String handles) {
        return new MethodHandlesMatcher(handles);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Does method handle: ").appendValue(handles);
    }

    @Override
    protected boolean matchesSafely(final Method method, final Description description) {

        if (method.isAnnotationPresent(Handles.class)) {
            final Handles annotation = method.getAnnotation(Handles.class);
            if (annotation.value().equals(handles)) {
                return true;
            }
        }

        description.appendText("Method: ")
                .appendValue(method.getName())
                .appendText(" does not handle: ")
                .appendValue(handles);

        return false;
    }
}
