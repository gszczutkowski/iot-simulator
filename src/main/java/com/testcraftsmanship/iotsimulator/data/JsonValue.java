package com.testcraftsmanship.iotsimulator.data;

import lombok.Getter;

import static com.testcraftsmanship.iotsimulator.data.JsonValueType.parseJsonValueType;

/**
 * Class contains json values type and value required for correct validation..
 */
@Getter
public class JsonValue {
    private final JsonValueType type;
    private final String value;

    /**
     * Instantiate object in which json value and type is extracted from json message part passed as an object.
     *
     * @param jsonMessagePart value of one json key
     */
    public JsonValue(Object jsonMessagePart) {
        this(parseJsonValueType(jsonMessagePart), jsonMessagePart.toString());
    }

    /**
     * Instantiate object in which json value and type is passed from type and value.
     *
     * @param type  represents json key type
     * @param value represents json key value for given type
     */
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
