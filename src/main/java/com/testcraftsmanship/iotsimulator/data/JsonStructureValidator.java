package com.testcraftsmanship.iotsimulator.data;


import org.json.JSONException;

import java.util.Stack;

import static com.amazonaws.util.StringUtils.isNullOrEmpty;


public final class JsonStructureValidator {
    private static final int MIN_JSON_LENGTH = 2;
    private JsonStructureValidator() {
    }

    public static void checkIfJsonHaveCorrectStructure(String json) {
        if (isNullOrEmpty(json)) {
            throw new IllegalArgumentException("Json argument can not be null nor empty");
        }
        Stack<Character> openBrackets = new Stack<>();
        char[] jsonInChars = json.toCharArray();
        if (jsonInChars.length < MIN_JSON_LENGTH) {
            throw new JSONException("Illegal Json content " + json);
        }
        boolean isFirst = true;
        for (char currentChar : jsonInChars) {
            if (isFirst) {
                if (currentChar == '{') {
                    openBrackets.add(currentChar);
                    isFirst = false;
                } else {
                    throw new JSONException("Illegal Json content " + json);
                }
            } else {
                if (openBrackets.isEmpty()) {
                    throw new JSONException("Illegal Json content " + json);
                } else if (isOpenBracketChar(currentChar)) {
                    openBrackets.add(currentChar);
                } else if (isTheCloseBracketAndNotMatchingLastOpenBracket(currentChar, openBrackets)) {
                    throw new JSONException("Illegal Json content " + json);
                }
            }
        }
    }

    private static boolean isOpenBracketChar(char theChar) {
        return theChar == '{' || theChar == '[';
    }

    private static boolean isTheCloseBracketAndNotMatchingLastOpenBracket(char theChar, Stack<Character> openBrackets) {
        return (theChar == '}' && openBrackets.pop() != '{') || (theChar == ']' && openBrackets.pop() != '[');
    }
}
