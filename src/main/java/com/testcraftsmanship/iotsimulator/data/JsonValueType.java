package com.testcraftsmanship.iotsimulator.data;

import org.json.JSONArray;
import org.json.JSONObject;

public enum JsonValueType {
    STRING, NUMBER, BOOLEAN, ARRAY, OBJECT;

    public static JsonValueType parseJsonValueType(Object object) {
        if (object instanceof String) {
            return JsonValueType.STRING;
        } else if (object instanceof Integer) {
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

    public boolean matches(Object object) {
        return equals(parseJsonValueType(object));
    }
}
