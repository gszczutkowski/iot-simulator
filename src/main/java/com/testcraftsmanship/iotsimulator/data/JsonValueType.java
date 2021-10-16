package com.testcraftsmanship.iotsimulator.data;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Enum represents types of json key.
 */
public enum JsonValueType {
    STRING, NUMBER, BOOLEAN, ARRAY, OBJECT;

    /**
     * Parses the object to JSON value type.
     *
     * @param object which should contain value in one of json types
     * @return json type of given object
     */
    public static JsonValueType parseJsonValueType(Object object) {
        if (object instanceof String) {
            return JsonValueType.STRING;
        } else if (object instanceof Number) {
            return JsonValueType.NUMBER;
        } else if (object instanceof Boolean) {
            return JsonValueType.BOOLEAN;
        } else if (object instanceof JSONObject) {
            return JsonValueType.OBJECT;
        } else if (object instanceof JSONArray) {
            return JsonValueType.ARRAY;
        } else {
            throw new IllegalArgumentException("Can't recognise the Json value type in the passed object");
        }
    }
}
