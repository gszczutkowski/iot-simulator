package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.utils.TestDataProvider;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonStructureValidatorTest implements TestDataProvider {
    @ParameterizedTest
    @MethodSource("illegalJsoStructure")
    public void shouldThrowIllegalArgumentExceptionForNullJsonValue(String json) {
        assertThrows(JSONException.class, () -> {
            JsonStructureValidator.checkIfJsonHasCorrectStructure(json);
        });
    }
}
