package com.testcraftsmanship.iotsimulator.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonValueTest {
    @ParameterizedTest
    @MethodSource("illegalJsonValues")
    public void shouldThrowIllegalArgumentExceptionWhileCreatingJsonValueFromIllegalJsonValueType(JsonValueType jsonValueType) {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonValue(jsonValueType, "example value");
        });
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhileCreatingJsonValueFromNullValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonValue(JsonValueType.STRING, null);
        });
    }

    @ParameterizedTest
    @MethodSource("correctJsonValues")
    public void shouldCreateJsonValueInstanceFromValidJsonValueType(JsonValueType jsonValueType, String actualValue,
                                                                    String expectedValue) {
        assertEquals(new JsonValue(jsonValueType, actualValue).toString(), expectedValue);
    }

    static Stream<Arguments> illegalJsonValues() {
        return Stream.of(
                Arguments.of(JsonValueType.OBJECT)
        );
    }

    static Stream<Arguments> correctJsonValues() {
        return Stream.of(
                Arguments.of(JsonValueType.STRING, "Correct param value", "\"Correct param value\""),
                Arguments.of(JsonValueType.NUMBER, "31", "31"),
                Arguments.of(JsonValueType.NUMBER, Long.valueOf(9223372036854775807L).toString(), "9223372036854775807"),
                Arguments.of(JsonValueType.NUMBER, Double.valueOf(300000.9999).toString(), "300000.9999"),
                Arguments.of(JsonValueType.NUMBER, Float.valueOf(2000.9999F).toString(), "2000.9999"),
                Arguments.of(JsonValueType.BOOLEAN, Boolean.valueOf(true).toString(), "true"),
                Arguments.of(JsonValueType.ARRAY, new JSONArray("[1,2]").toString(), "[1,2]")
        );
    }
}
