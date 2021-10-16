package com.testcraftsmanship.iotsimulator.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.testcraftsmanship.iotsimulator.constant.ParserConstants.paramContentRegex;
import static com.testcraftsmanship.iotsimulator.data.JsonStructureValidator.checkIfJsonHasCorrectStructure;

/**
 * Class responsible for comparing two JSONs with different rules.
 */
@Slf4j
public final class JsonMessageMatcher {

    private JsonMessageMatcher() {
    }

    /**
     * Returns true when actual and expected JSON are the same. If value of any JSON key does not
     * match then to return true value for that key should be regular expression matching value from second JSON.
     * If strict is true then both JSONs have to have exactly the same arguments if strict is set to false then
     * arguments that are only in one JSON are excluded from comparison.
     *
     * @param actual   JSON as string
     * @param expected JSON as string
     * @param strict   defines whether include to matching keys that are only in one JSON
     * @return result of the matching
     */
    public static boolean jsonMatch(String actual, String expected, boolean strict) {
        log.debug("Comparing JSON {} with JSON {}", actual, expected);
        return jsonGenericMatch(new ComparatorWithMask(), actual, expected, strict);
    }

    /**
     * Returns true when both JSONs have exactly the same keys and strict is true. When strict is false then
     * one JSON have to contain all keys from the other JSON.
     *
     * @param actual   JSON as string
     * @param expected JSON as string
     * @param strict   defines whether include to matching keys that are only in one JSON
     * @return result of the matching
     */
    public static boolean jsonStructureMatch(String actual, String expected, boolean strict) {
        log.debug("Comparing structure of JSON {} with JSON {}", actual, expected);
        return jsonGenericMatch(new JsonStructureComparator(), actual, expected, strict);
    }

    private static boolean jsonGenericMatch(Comparator<JsonNode> comparator, String actual, String expected, boolean strict) {
        if (actual == null || expected == null) {
            throw new IllegalArgumentException("Arguments can not be null");
        }
        checkIfJsonHasCorrectStructure(actual);
        checkIfJsonHasCorrectStructure(expected);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode mappedJson1 = mapper.readTree(new JSONObject(actual).toString());
            JsonNode mappedJson2 = mapper.readTree(new JSONObject(expected).toString());
            if (!strict) {
                deepMissingFieldsRemover(mappedJson1, mappedJson2);
            }
            return mappedJson1.equals(comparator, mappedJson2);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Problem with JSON processing");
        }
    }

    private static void deepMissingFieldsRemover(JsonNode actual, JsonNode expectedNotStrict) {
        Iterator<String> fieldNameIterator = actual.fieldNames();
        while (fieldNameIterator.hasNext()) {
            String fieldName = fieldNameIterator.next();
            if (expectedNotStrict.hasNonNull(fieldName)) {
                JsonNode childActual = actual.get(fieldName);
                JsonNode childExpected = expectedNotStrict.get(fieldName);
                if (childActual instanceof ObjectNode && childExpected instanceof ObjectNode) {
                    deepMissingFieldsRemover(childActual, childExpected);
                }
            } else {
                fieldNameIterator.remove();
            }
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
                Optional<String> expectedRegexp = getFirstGroup(paramContentRegex(), s1);
                if (expectedRegexp.isPresent() && s2.matches(expectedRegexp.get())) {
                    return 0;
                }
            } else if (s2.matches(paramContentRegex())) {
                Optional<String> expectedRegexp = getFirstGroup(paramContentRegex(), s2);
                if (expectedRegexp.isPresent() && s1.matches(expectedRegexp.get())) {
                    return 0;
                }
            }
            return 1;
        }

        private String getValueAsString(JsonNode node) {
            if (node instanceof TextNode || node instanceof NumericNode) {
                return node.asText();
            } else {
                throw new IllegalArgumentException("Cannot extract value from node type " + node.getNodeType().name());
            }
        }

        private Optional<String> getFirstGroup(String regex, String text) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return Optional.of(matcher.group(1));
            } else {
                return Optional.empty();
            }
        }
    }

    private static class JsonStructureComparator implements Comparator<JsonNode> {
        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            String s1 = getValueAsString(o1);
            String s2 = getValueAsString(o2);
            if (s1.matches(paramContentRegex()) || s2.matches(paramContentRegex())) {
                return 0;
            } else {
                return 1;
            }
        }

        private String getValueAsString(JsonNode node) {
            if (node instanceof TextNode || node instanceof NumericNode) {
                return node.asText();
            } else {
                throw new IllegalArgumentException("Cannot extract value from node type " + node.getNodeType().name());
            }
        }
    }
}
