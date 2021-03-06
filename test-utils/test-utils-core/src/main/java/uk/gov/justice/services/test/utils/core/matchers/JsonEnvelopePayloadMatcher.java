package uk.gov.justice.services.test.utils.core.matchers;

import javax.json.JsonObject;

import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.matchers.IsJson;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matches the Json Payload part of a JsonEnvelope. See {@link JsonEnvelopeMatcher} for usage
 * example.
 */
public class JsonEnvelopePayloadMatcher extends TypeSafeDiagnosingMatcher<JsonObject> {

    private IsJson<Object> matcher;

    /**
     * Use {@link JsonEnvelopePayloadMatcher#payload} or {@link JsonEnvelopePayloadMatcher#payloadIsJson}
     */
    @Deprecated
    public static JsonEnvelopePayloadMatcher payLoad() {
        return new JsonEnvelopePayloadMatcher();
    }

    public static JsonEnvelopePayloadMatcher payload() {
        return new JsonEnvelopePayloadMatcher();
    }

    public static JsonEnvelopePayloadMatcher payloadIsJson(final Matcher<? super ReadContext> matcher) {
        return new JsonEnvelopePayloadMatcher().isJson(matcher);
    }

    @Override
    public void describeTo(final Description description) {
        description
                .appendText("Payload ")
                .appendDescriptionOf(matcher)
                .appendText(" ");
    }

    public JsonEnvelopePayloadMatcher isJson(final Matcher<? super ReadContext> matcher) {
        this.matcher = new IsJson<>(matcher);
        return this;
    }

    @Override
    protected boolean matchesSafely(final JsonObject jsonObject, final Description description) {
        final String jsonAsString = jsonObject.toString();

        if (!matcher.matches(jsonAsString)) {
            description.appendText("Payload ");
            matcher.describeMismatch(jsonAsString, description);
            return false;
        }

        return true;
    }
}