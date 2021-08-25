package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.utils.TestDataProvider;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class JsonMessageMatcherTest implements TestDataProvider {
    @ParameterizedTest
    @MethodSource("jsonAsNull")
    public void shouldThrowIllegalArgumentExceptionForNullJsonValue(String json1, String json2) {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonMessageMatcher.jsonMatch(json1, json2);
        });
    }

    @ParameterizedTest
    @MethodSource("illegalJsonPair")
    public void shouldThrowJSONExceptionWhenIllegalJson(String json1, String json2) {
        assertThrows(JSONException.class, () -> {
            JsonMessageMatcher.jsonMatch(json1, json2);
        });
    }

    @ParameterizedTest
    @MethodSource("jsonValueMatchingRegexMask")
    public void shouldMatchJsonValuesByRegexp(String json1, String json2) {
        assertTrue(JsonMessageMatcher.jsonMatch(json1, json2));
    }

    @ParameterizedTest
    @MethodSource("jsonValueDoesNotMatchRegexpMask")
    public void shouldNotMatchJsonValuesByWrongRegexp(String json1, String json2) {
        assertFalse(JsonMessageMatcher.jsonMatch(json1, json2));
    }

    @ParameterizedTest
    @MethodSource("jsonMatchByValue")
    public void shouldMatchJsonWithSameValues(String json1, String json2) {
        assertTrue(JsonMessageMatcher.jsonMatch(json1, json2));
    }

    @ParameterizedTest
    @MethodSource("jsonNotMatchByValue")
    public void shouldNotMatchJsonWithDifferentValues(String json1, String json2) {
        assertFalse(JsonMessageMatcher.jsonMatch(json1, json2));
    }
}
