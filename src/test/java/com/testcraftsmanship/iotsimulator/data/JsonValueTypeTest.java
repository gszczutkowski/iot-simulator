package com.testcraftsmanship.iotsimulator.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonValueTypeTest {
    @ParameterizedTest
    @MethodSource("jsonParameterValues")
    public void itShouldBePossibleToParseDesiredObjectToJsonValueType(Object jsonValue, JsonValueType jsonValueType) {
        assertEquals(JsonValueType.parseJsonValueType(jsonValue), jsonValueType);
    }

    @ParameterizedTest
    @MethodSource("illegalJsonParameterValues")
    public void exceptionShouldBeThrownInJsonValueTypeWhenParsingUnknownObject(Object jsonValue) {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonValueType.parseJsonValueType(jsonValue);
        });
    }

    @Test
    public void exceptionShouldBeThrownInJsonValueTypeWhenNullPassed() {
        assertThrows(IllegalArgumentException.class, () -> {
            JsonValueType.parseJsonValueType(null);
        });
    }

    static Stream<Arguments> jsonParameterValues() {
        return Stream.of(
                Arguments.of("Correct param value", JsonValueType.STRING),
                Arguments.of(31, JsonValueType.NUMBER),
                Arguments.of(1000000000000000000L, JsonValueType.NUMBER),
                Arguments.of(300000.9999, JsonValueType.NUMBER),
                Arguments.of(2000.9999f, JsonValueType.NUMBER),
                Arguments.of(true, JsonValueType.BOOLEAN),
                Arguments.of(new JSONObject(), JsonValueType.OBJECT),
                Arguments.of(new JSONArray("[1,2]"), JsonValueType.ARRAY)
        );
    }

    static Stream<Arguments> illegalJsonParameterValues() {
        return Stream.of(
                Arguments.of(new Object()),
                Arguments.of(new ArrayList<>()),
                Arguments.of('A')
        );
    }
}
