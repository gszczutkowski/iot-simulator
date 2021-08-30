package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.utils.TestDataProvider;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMessageMatcherTest implements TestDataProvider {
    private static final boolean STRICT_MATCHING_ENABLED = true;
    private static final boolean STRICT_MATCHING_DISABLED = false;

    @ParameterizedTest
    @MethodSource("jsonAsNull")
    public void shouldThrowIllegalArgumentExceptionForNullJsonValue(String json1, String json2) {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED);
        });
    }

    @ParameterizedTest
    @MethodSource("illegalJsonPair")
    public void shouldThrowJSONExceptionWhenIllegalJson(String json1, String json2) {
        assertThrows(JSONException.class, () -> {
            JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED);
        });
    }

    @ParameterizedTest
    @MethodSource("jsonValueMatchingRegexMask")
    public void shouldMatchJsonValuesByRegexp(String json1, String json2) {
        assertTrue(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED));
    }

    @ParameterizedTest
    @MethodSource("jsonValueDoesNotMatchRegexpMask")
    public void shouldNotMatchJsonValuesByWrongRegexp(String json1, String json2) {
        assertFalse(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED));
    }

    @ParameterizedTest
    @MethodSource("jsonMatchByValue")
    public void shouldMatchJsonWithSameValues(String json1, String json2) {
        assertTrue(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED));
    }

    @ParameterizedTest
    @MethodSource("jsonNotMatchByValue")
    public void shouldNotMatchJsonWithDifferentValues(String json1, String json2) {
        assertFalse(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_ENABLED));
    }

    @ParameterizedTest
    @MethodSource("jsonMatchByValueNoStrict")
    public void shouldMatchByValueWithStrictDisabled(String json1, String json2) {
        assertTrue(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_DISABLED));
    }

    @ParameterizedTest
    @MethodSource("jsonNotMatchByValueNoStrict")
    public void shouldNotMatchByValueWithStrictDisabled(String json1, String json2) {
        assertFalse(JsonMessageMatcher.jsonMatch(json1, json2, STRICT_MATCHING_DISABLED));
    }
}
