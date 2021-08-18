package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public interface SubscriberTestDataProvider {
    static Stream<Arguments> strictMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 23}"),
                Arguments.of("+/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}", "{'tmp': 23}"),
                Arguments.of("myhome/+/livingroom", "myhome/groundfloor/livingroom", "{'name': 'my room'}", "{'name': 'my room'}"),
                Arguments.of("myhome/groundfloor/+", "myhome/groundfloor/livingroom", "{'name': '{[A-Za-z ]+}'}", "{'name': 'My room'}"),
                Arguments.of("myhome/#", "myhome/groundfloor/livingroom", "{'tmp': [10, 23]}", "{'tmp': [10, 23]}"),
                Arguments.of("myhome/#", "myhome/groundfloor/livingroom", "{'tmp': [10, 23]}", "{'tmp': [23, 10]}")
        );
    }

    static Stream<Arguments> strictNotMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 23, 'id': 2}"),
                Arguments.of("+/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}", "{'tmp': 23, 'id': 2}"),
                Arguments.of("myhome/groundfloor/+", "myhome/groundfloor/livingroom", "{'name': '{[A-Za-z ]+}'}", "{'id': 'AA', 'name': 'My room'}"),
                Arguments.of("myhome/#", "myhome/groundfloor/livingroom", "{'tmp': [10, 23]}", "{'tmp': [10]}"),
                Arguments.of("myhome/#", "myhome/groundfloor/livingroom", "{'tmp': [10, 23]}", "{'tmp': [1023]}")
        );
    }

    static Stream<Arguments> allMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 1000}"),
                Arguments.of("+/groundfloor", "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor", "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26, 'name': 'Bathroom'}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "{'temp': [22, 21, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    static Stream<Arguments> anyMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 'AS1000'}"),
                Arguments.of("+/groundfloor", "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor", "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "{'temp': [22, 21, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    static Stream<Arguments> failedStrictMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("+/groundfloor/livingroom", "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("myhome/+/livingroom", "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("myhome/groundfloor/+", "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("myhome/#", "myhome/groundfloor/livingroom", "{'tmp': 23}")
        );
    }

    static Stream<Arguments> messagesOnWrongTopic() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "myhome/groundfloor/bathroom", "{'tmp': 23}"),
                Arguments.of("+/groundfloor/livingroom", "myhome/groundfloor/bathroom", "{'tmp': 23}"),
                Arguments.of("myhome/#", "yourhome/groundfloor/livingroom", "{'tmp': 23}")
        );
    }

    static Stream<Arguments> matchingMessagesWithTopics() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("+/groundfloor/livingroom", "{'tmp': 23, 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23, 'name': 'Livingroom'}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24]}",
                        "myhome/groundfloor", "{'tmp': '{[0-9,\\]\\[]+}'}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    static Stream<Arguments> notMatchingMessagesWithTopics() {
        return Stream.of(
                Arguments.of("myhome/groundfloor/livingroom", "{'tmp': '{[a-z]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of("+/groundfloor/livingroom", "{'tmp': '{[a-z]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23, 'name': 'Livingroom'}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24]}",
                        "myhome/groundfloor", "{'tmp': '{[0-9]+}'}"),
                Arguments.of("myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 21}}")
        );
    }
}
