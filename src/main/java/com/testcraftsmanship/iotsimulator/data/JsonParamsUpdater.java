package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.exception.MappingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Map;

import static com.testcraftsmanship.iotsimulator.constant.ParserConstants.paramRegexWith;
import static com.testcraftsmanship.iotsimulator.constant.ParserConstants.paramWith;

@Slf4j
public class JsonParamsUpdater {
    private final String jsonMessage;
    private final Map<String, String> paramsWithValues;

    public JsonParamsUpdater(String jsonMessage, Map<String, String> paramsWithValues) {
        if (jsonMessage == null || paramsWithValues == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        this.jsonMessage = new JSONObject(jsonMessage).toString();
        this.paramsWithValues = paramsWithValues;
    }

    public JSONObject updateJsonParamsWithValues() throws MappingException {
        log.info("Updating message {} with passed parameters", jsonMessage);
        String payload = jsonMessage;
        for (Map.Entry<String, String> paramValue : paramsWithValues.entrySet()) {
            if (payload.contains(getParam(paramValue.getKey()))) {
                payload = payload.replaceAll(getRegexParam(paramValue.getKey()), paramValue.getValue());
                log.debug("Param {} has been found and to be updated to {}",
                        getParam(paramValue.getKey()), paramValue.getValue());
            } else {
                log.debug("Param {} has not been found so will not be updated",
                        getParam(paramValue.getKey()));
                throw new MappingException("Param {} does not have representative in json");
            }
        }
        log.debug("Updated json message: {}", payload);
        return new JSONObject(payload);
    }

    private static String getParam(String param) {
        return "\"" + paramWith(param) + "\"";
    }

    private static String getRegexParam(String param) {
        return "\"" + paramRegexWith(param) + "\"";
    }
}
