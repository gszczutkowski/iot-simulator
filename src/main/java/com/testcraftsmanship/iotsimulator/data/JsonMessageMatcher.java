package com.testcraftsmanship.iotsimulator.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.testcraftsmanship.iotsimulator.data.ParserConstants.paramContentRegex;

@Slf4j
public final class JsonMessageMatcher {

    private JsonMessageMatcher() {
    }

    public static boolean jsonMatch(String json1, String json2) {
        log.info("Comparing json {} with json {}", json1, json2);
        if (json1 == null || json2 == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode mappedJson1 = mapper.readTree(new JSONObject(json1).toString());
            JsonNode mappedJson2 = mapper.readTree(new JSONObject(json2).toString());
            return mappedJson1.equals(new ComparatorWithMask(), mappedJson2);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Problem with json processing");
        }
    }

    private static class ComparatorWithMask implements Comparator<JsonNode> {
        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            String s1 = getValueAsString(o1);
            String s2 = getValueAsString(o2);
            if (s1.matches(paramContentRegex())) {
                String expectedRegexp = getFirstGroup(paramContentRegex(), s1);
                if (s2.matches(expectedRegexp)) {
                    return 0;
                }
            } else if (s2.matches(paramContentRegex())) {
                String expectedRegexp = getFirstGroup(paramContentRegex(), s2);
                if (s1.matches(expectedRegexp)) {
                    return 0;
                }
            }
            return 1;
        }

        private String getValueAsString(JsonNode node) {
            if (node instanceof TextNode) {
                return ((TextNode) node).asText();
            } else if (node instanceof NumericNode) {
                return ((NumericNode) node).asText();
            } else {
                throw new IllegalArgumentException("Cannot extract value from node type " + node.getNodeType().name());
            }
        }

        private String getFirstGroup(String regex, String text) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            matcher.find();
            return matcher.group(1);
        }
    }
}
