package com.testcraftsmanship.iotsimulator.data;

public final class ParserConstants {
    private ParserConstants() {
    }

    private static final String PARAM_CONTENT = "(.+)";

    public static String paramWith(String with) {
        return String.format("{%s}", with);
    }

    public static String paramRegexWith(String with) {
        return String.format("\\{%s\\}", with);
    }

    public static String paramContentRegex() {
        return paramRegexWith(PARAM_CONTENT);
    }
}
