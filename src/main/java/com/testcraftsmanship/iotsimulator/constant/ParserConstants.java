package com.testcraftsmanship.iotsimulator.constant;

public final class ParserConstants {
    private static final String PARAM_CONTENT = "(.+)";
    private static final char STARTING_PARAMETER_CHAR = '{';
    private static final char CLOSING_PARAMETER_CHAR = '}';

    private ParserConstants() {
    }

    /**
     * Returns text representation in json of the parameter available in the value. For json {"id": 30, "name": "{nameParam}"}
     * with should have value nameParam and parameter representation is  {nameParam}.
     *
     * @param with parameter name
     * @return text of the parameter available in json
     */
    public static String paramWith(String with) {
        return String.format(STARTING_PARAMETER_CHAR + "%s" + CLOSING_PARAMETER_CHAR, with);
    }

    /**
     * Returns regular expression representation in json of the parameter available in the value. When we want to map json
     * value with use of regular expressions then we can define it in parameter. When in with we pass [0-9]+ then in
     * result we get parameter representation {[0-9]+}.
     *
     * @param with parameter containing regular expression
     * @return regular expression as parameter available in json
     */
    public static String paramRegexWith(String with) {
        return String.format("\\" + STARTING_PARAMETER_CHAR + "%s\\" + CLOSING_PARAMETER_CHAR, with);
    }

    /**
     * Returns string which represents regular expression matching to parameter json value. This value has to match
     * to value {nameParam} in json {"id": 30, "name": "{nameParam}"}.
     *
     * @return regular expression matching parameters in json value
     */
    public static String paramContentRegex() {
        return paramRegexWith(PARAM_CONTENT);
    }
}
