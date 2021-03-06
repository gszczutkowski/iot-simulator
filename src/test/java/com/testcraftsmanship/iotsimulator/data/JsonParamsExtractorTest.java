package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.exception.MappingException;
import com.testcraftsmanship.iotsimulator.utils.TestDataProvider;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonParamsExtractorTest implements TestDataProvider {
    @ParameterizedTest
    @MethodSource("positiveExtractedParamsFromStrictMasks")
    public void shouldStrictlyExtractValuesToParams(String json, String mask, Map<String, String> result)
            throws MappingException {
        final boolean strictMatching = true;
        JsonParamsExtractor paramsExtractor = new JsonParamsExtractor(json, mask, strictMatching);
        assertThat(paramsExtractor.getParamsWithValues()).containsExactlyInAnyOrderEntriesOf(result);
    }

    @ParameterizedTest
    @MethodSource("positiveExtractedParamsFromNotStrictMasks")
    public void shouldNotStrictlyExtractValuesToParams(String json, String mask, Map<String, String> result)
            throws MappingException {
        final boolean strictMatching = false;
        JsonParamsExtractor paramsExtractor = new JsonParamsExtractor(json, mask, strictMatching);
        assertThat(paramsExtractor.getParamsWithValues()).containsExactlyInAnyOrderEntriesOf(result);
    }

    @ParameterizedTest
    @MethodSource("masksJsonWithNoParamsValues")
    public void shouldStrictlyReturnNoParamsWhenNoParamsInMask(String json, String mask)
            throws MappingException {
        final boolean strictMatching = true;
        Map<String, String> result = new HashMap<>();
        JsonParamsExtractor paramsExtractor = new JsonParamsExtractor(json, mask, strictMatching);
        assertThat(paramsExtractor.getParamsWithValues()).containsExactlyInAnyOrderEntriesOf(result);
    }

    @ParameterizedTest
    @MethodSource("jsonMatchByValueNoStrict")
    public void shouldReturnEmptyMapWhenMatchByValueWithStrictDisabled(String json, String mask) throws MappingException {
        Map<String, String> result = new HashMap<>();
        JsonParamsExtractor paramsExtractor = new JsonParamsExtractor(json, mask, false);
        assertThat(paramsExtractor.getParamsWithValues()).containsExactlyInAnyOrderEntriesOf(result);
    }

    @ParameterizedTest
    @MethodSource("jsonNotMatchByValueNoStrict")
    public void shouldThrowJSONExceptionWhenJsonNotMatchByValueWithStrictDisabled(String json, String mask) {
        assertThrows(MappingException.class, () -> new JsonParamsExtractor(json, mask, false));
    }

    @ParameterizedTest
    @MethodSource("jsonAsNull")
    public void shouldThrowIllegalArgumentExceptionForNullJsonValue(String json, String mask) {
        assertThrows(IllegalArgumentException.class, () -> new JsonParamsExtractor(json, mask, true));
    }

    @ParameterizedTest
    @MethodSource("illegalJsonPair")
    public void shouldThrowJSONExceptionWhenIllegalJson(String json, String mask) {
        assertThrows(JSONException.class, () -> new JsonParamsExtractor(json, mask, true));
    }

    @ParameterizedTest
    @MethodSource("maskJsonThrowingMappingException")
    public void shouldThrowMappingExceptionWhenJsonDoesNotMatchMask(String json, String mask, boolean strict) {
        assertThrows(MappingException.class, () -> new JsonParamsExtractor(json, mask, strict));
    }
}
