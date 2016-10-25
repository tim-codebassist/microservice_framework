package uk.gov.justice.services.test.utils.core.matchers;

import static uk.gov.justice.services.test.utils.core.helper.ServiceComponents.isNotServiceComponent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HandlerClassMatcher extends TypeSafeDiagnosingMatcher<Class<?>> {

    private Matcher matcher;

    public static HandlerClassMatcher isHandlerClass() {
        return new HandlerClassMatcher();
    }

    public HandlerClassMatcher with(final Matcher matcher) {
        this.matcher = matcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(final Class<?> handlerClass, final Description description) {

        if (isNotServiceComponent(handlerClass)) {
            description.appendValue(handlerClass.getName())
                    .appendText(" is not annotated as a Service Component");
            return false;
        }

        if (!matcher.matches(handlerClass)) {
            matcher.describeMismatch(handlerClass, description);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(final Description description) {

    }

}
