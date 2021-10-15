package com.testcraftsmanship.iotsimulator.data;

import com.testcraftsmanship.iotsimulator.exception.MappingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Map;

import static com.testcraftsmanship.iotsimulator.constant.ParserConstants.paramRegexWith;
import static com.testcraftsmanship.iotsimulator.constant.ParserConstants.paramWith;
import static com.testcraftsmanship.iotsimulator.data.JsonStructureValidator.checkIfJsonHasCorrectStructure;

/**
 * Class responsible for updating parameters in json with given values.
 *
 * @author Grzegorz Szczutkowski
 * @author www.testcraftsmanship.com
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class JsonParamsUpdater {
    private final String jsonMessage;
    private final Map<String, String> paramsWithValues;

    /**
     * Construct an object with JSON that will be filled in with params from the map. When as an jsonMessage we
     * pass {"id": "{idParam}", "name": "{nameParam}": "location":"PL"} in paramsWithValues with pass map with key/values
     * idParam:1, nameParam:"Tom" then in created object we will be having given json {"id": 1, "name": "Tom": "location":"PL"}.
     *
     * @param jsonMessage      containing parameters that will be replaced with values from map
     * @param paramsWithValues to be filled in the JSON
     */
    public JsonParamsUpdater(String jsonMessage, Map<String, String> paramsWithValues) {
        if (jsonMessage == null || paramsWithValues == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        checkIfJsonHasCorrectStructure(jsonMessage);
        this.jsonMessage = new JSONObject(jsonMessage).toString();
        this.paramsWithValues = paramsWithValues;
    }

    /**
     * Make a JSONObject from JSON text in which parameters are replaced with the values given in the constructor.
     *
     * @return JSON with parameters replaced with values
     * @throws MappingException when mapping not possible
     */
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
                throw new MappingException("Param {} does not have representative in JSON");
            }
        }
        log.debug("Updated JSON message: {}", payload);
        return new JSONObject(payload);
    }

    private static String getParam(String param) {
        return "\"" + paramWith(param) + "\"";
    }

    private static String getRegexParam(String param) {
        return "\"" + paramRegexWith(param) + "\"";
    }
}
