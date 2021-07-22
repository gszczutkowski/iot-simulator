package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.exception.MappingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.testcraftsmanship.iotsimulator.data.JsonValueType.parseJsonValueType;
import static com.testcraftsmanship.iotsimulator.data.ParserConstants.paramContentRegex;

@Slf4j
public class JsonParamsExtractor {
    private final Map<String, JsonValue> jsonParamsWithValues;
    private final boolean strictMatching;

    public JsonParamsExtractor(String jsonMessage, String jsonMask, boolean strict)
            throws MappingException {
        if (jsonMessage == null || jsonMask == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        log.info("Extracting values from json {} based on mask {} with strict set to {}",
                jsonMessage, jsonMask, strict);
        this.strictMatching = strict;
        this.jsonParamsWithValues = extractParamsValuesFromMessage(new JSONObject(jsonMessage), new JSONObject(jsonMask));
        log.info("Extracted params: {}", jsonParamsWithValues);
    }

    public Map<String, String> getParamsWithValues() {
        return jsonParamsWithValues.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
    }

    private Map<String, JsonValue> extractParamsValuesFromMessage(JSONObject jsonMessage, JSONObject jsonMask)
            throws MappingException {
        if (strictMatching && !jsonMask.keySet().equals(jsonMessage.keySet())) {
            throw new MappingException("Mask is not matching the parsed json. Key sets are different:"
                    + jsonMask.keySet() + " " + jsonMessage.keySet());
        }
        Map<String, JsonValue> attributesWithValues = new HashMap<>();
        for (String key : jsonMask.keySet()) {
            Object messagePart;
            try {
                messagePart = jsonMessage.get(key);
            } catch (org.json.JSONException e) {
                throw new MappingException("Unable to find value in json for parameter " + key);
            }
            Object maskPart = jsonMask.get(key);
            if (isJsonObject(messagePart) && isJsonObject(maskPart)) {
                JSONObject jsonMessageObject = (JSONObject) messagePart;
                JSONObject jsonMaskObject = (JSONObject) maskPart;
                attributesWithValues.putAll(extractParamsValuesFromMessage(jsonMessageObject, jsonMaskObject));
            } else if (isArray(messagePart) && isArray(maskPart)) {
                attributesWithValues.putAll(extractParamFromArray((JSONArray) messagePart, (JSONArray) maskPart));
            } else {
                attributesWithValues.putAll(extractParamFromPart(messagePart, maskPart));
            }
        }
        return attributesWithValues;
    }

    private Map<String, JsonValue> extractParamFromPart(Object jsonMessagePart, Object jsonMaskPart)
            throws MappingException {
        Map<String, JsonValue> attributesWithValues = new HashMap<>();
        if (isString(jsonMaskPart)) {
            Pattern pattern = Pattern.compile(paramContentRegex());
            Matcher matcher = pattern.matcher(jsonMaskPart.toString());
            if (matcher.find()) {
                JsonValue jsonMessagePartValue = new JsonValue(parseJsonValueType(jsonMessagePart), jsonMessagePart.toString());
                String keyValue = matcher.group(1);
                attributesWithValues.put(keyValue, jsonMessagePartValue);
                return attributesWithValues;
            }
        }
        if (jsonMessagePart.equals(jsonMaskPart)) {
            return attributesWithValues;
        } else {
            throw new MappingException("Mask is not matching the parsed json. Value for mask "
                    + jsonMaskPart + " differs from message " + jsonMessagePart);
        }
    }

    private Map<String, JsonValue> extractParamFromArray(JSONArray jsonMessageArray, JSONArray jsonMaskArray)
            throws MappingException {
        if (strictMatching && jsonMaskArray.length() != jsonMessageArray.length()) {
            throw new MappingException("Arrays length in mask and message are different.");
        }

        Map<String, JsonValue> attributesWithValues = new HashMap<>();
        for (int i = 0; i < jsonMaskArray.length(); i++) {
            if (jsonMaskArray.get(i) instanceof JSONObject) {
                attributesWithValues = extractParamsValuesFromMessage(
                        (JSONObject) jsonMessageArray.get(i), (JSONObject) jsonMaskArray.get(i));
            }
        }
        return attributesWithValues;
    }

    private boolean isArray(Object object) {
        return JsonValueType.ARRAY.equals(parseJsonValueType(object));
    }

    private boolean isJsonObject(Object object) {
        return JsonValueType.OBJECT.equals(parseJsonValueType(object));
    }

    private boolean isString(Object object) {
        return JsonValueType.STRING.equals(parseJsonValueType(object));
    }
}
