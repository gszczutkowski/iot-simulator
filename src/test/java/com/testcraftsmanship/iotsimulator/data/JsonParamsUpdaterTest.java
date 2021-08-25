package com.testcraftsmanship.iotsimulator.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraftsmanship.iotsimulator.utils.TestDataProvider;
import com.testcraftsmanship.iotsimulator.exception.MappingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonParamsUpdaterTest implements TestDataProvider {
    @Test
    public void shouldThrowIllegalArgumentExceptionWhenJsonIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonParamsUpdater("", new HashMap<>());
        });
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenJsonIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonParamsUpdater(null, new HashMap<>());
        });
    }

    @ParameterizedTest
    @MethodSource("positiveParamsWithUpdatedJson")
    public void jsonWithParametrizedValuesShouldBeUpdated(Map<String, String> params,
                                                               String json, String updatedJson) throws MappingException {
        JSONObject expectedJson = new JSONObject(updatedJson);
        JsonParamsUpdater paramsUpdater = new JsonParamsUpdater(json, params);
        JSONObject actualJson = paramsUpdater.updateJsonParamsWithValues();
        assertJsonObjectsEquals(actualJson, expectedJson);
    }

    @ParameterizedTest
    @MethodSource("positiveParamsWithUpdatedJson")
    public void shouldStrictlyReturnNoParamsWhenNoParamsInMask(Map<String, String> params,
                                                               String json, String updatedJson) throws MappingException {
        JSONObject expectedJson = new JSONObject(updatedJson);
        JsonParamsUpdater paramsUpdater = new JsonParamsUpdater(json, params);
        JSONObject actualJson = paramsUpdater.updateJsonParamsWithValues();
        assertJsonObjectsEquals(actualJson, expectedJson);
    }

    @ParameterizedTest
    @MethodSource("jsonAndParamsNull")
    public void shouldThrowIllegalArgumentExceptionForNullJsonValue(String json, Map<String, String> params) {
        assertThrows(IllegalArgumentException.class, () -> {
            new JsonParamsUpdater(json, params);
        });
    }

    @ParameterizedTest
    @MethodSource("illegalUpdaterJson")
    public void shouldThrowJSONExceptionWhenIllegalJson(String json1) {
        assertThrows(JSONException.class, () -> {
            new JsonParamsUpdater(json1, new HashMap<>());
        });
    }

    @ParameterizedTest
    @MethodSource("jsonAndParamThrowingMappingException")
    public void shouldThrowMappingExceptionWhenParamsNotMatchingThoseInJson(String json, Map<String, String> params) {
        assertThrows(MappingException.class, () -> {
            new JsonParamsUpdater(json, params).updateJsonParamsWithValues();
        });
    }

    private void assertJsonObjectsEquals(JSONObject json1, JSONObject json2) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mappedJson1 = null;
        JsonNode mappedJson2 = null;
        try {
            mappedJson1 = mapper.readTree(json1.toString());
            mappedJson2 = mapper.readTree(json2.toString());
            assertThat(mappedJson1.equals(mappedJson2)).isTrue();
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Wrong value passed to assertion method");
        }
    }
}
