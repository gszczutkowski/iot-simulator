package com.testcraftsmanship.iotsimulator.data;

import lombok.Getter;

@Getter
public class JsonValue {
    private JsonValueType type;
    private String value;

    public JsonValue(JsonValueType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        if (JsonValueType.STRING.equals(type)) {
            return "\"" + value + "\"";
        } else {
            return value;
        }
    }
}
