package uk.gov.justice.services.test.utils.core.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class HandlerMatcher extends TypeSafeDiagnosingMatcher<Object> {

    private Matcher matcher;

    public static HandlerMatcher isHandler() {
        return new HandlerMatcher();
    }

    public HandlerMatcher with(final Matcher matcher) {
        this.matcher = matcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(final Object handler, final Description description) {
        return new HandlerClassMatcher()
                .with(matcher)
                .matchesSafely(handler.getClass(), description);
    }

    @Override
    public void describeTo(final Description description) {

    }

}
