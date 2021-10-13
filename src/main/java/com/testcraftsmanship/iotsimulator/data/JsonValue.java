package com.testcraftsmanship.iotsimulator.data;

import lombok.Getter;

@Getter
public class JsonValue {
    private JsonValueType type;
    private String value;

    public JsonValue(JsonValueType type, String value) {
        if (isNotCorrectValueForType(type, value)) {
            throw new IllegalArgumentException("JsonValue can't be created fromm json type " + type
                    + " and value " + value);
        }
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

    @SuppressWarnings("PMD.UselessParentheses")
    private boolean isNotCorrectValueForType(JsonValueType attrType, String attrValue) {
        return attrValue == null || JsonValueType.OBJECT.equals(attrType)
                || (!JsonValueType.STRING.equals(attrType) && attrValue.isEmpty());
    }
}
