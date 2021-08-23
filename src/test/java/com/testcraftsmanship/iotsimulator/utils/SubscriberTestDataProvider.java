package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public interface SubscriberTestDataProvider {
    static Stream<Arguments> strictMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/livingroom", "{'tmp': 23}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of(
                        "+/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of(
                        "myhome/+/livingroom", "{'name': 'my room'}",
                        "myhome/groundfloor/livingroom", "{'name': 'my room'}"),
                Arguments.of(
                        "myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        "myhome/groundfloor/livingroom", "{'name': 'My room'}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [10, 23]}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [23, 10]}")
        );
    }

    static Stream<Arguments> strictNotMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/livingroom", "{'tmp': 23}",
                        "myhome/groundfloor/room", "{'tmp': 23}"),
                Arguments.of(
                        "+/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 'ID'}"),
                Arguments.of(
                        "myhome/+/livingroom", "{'name': 'my room', 'id' = 2}",
                        "myhome/groundfloor/livingroom", "{'name': 'my room', 'id' = 3}"),
                Arguments.of(
                        "myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        "myhome/groundfloor/livingroom", "{'name': 'My room 77', 'id' = 3"),
                Arguments.of(
                        "myhome/#", "{'tmp': 23}",
                        "myhome/groundfloor/livingroom", "{'tmp': [23]}")
        );
    }

    static Stream<Arguments> strictDisabledMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/livingroom", "{'tmp': 23}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23, 'id': 2}"),
                Arguments.of(
                        "+/groundfloor/livingroom", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23, 'id': 2}"),
                Arguments.of(
                        "myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        "myhome/groundfloor/livingroom", "{'id': 'AA', 'name': 'My room'}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [10]}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [1023]}")
        );
    }

    static Stream<Arguments> strictDisabledNotMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        "myhome/groundfloor/livingroom", "{'id': 'AA', 'name': 12}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [10]}"),
                Arguments.of(
                        "myhome/#", "{'tmp': [10, 23]}",
                        "myhome/groundfloor/livingroom", "{'tmp': [1023]}")
        );
    }

    static Stream<Arguments> allMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 1000}"),
                Arguments.of(
                        "+/groundfloor", "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor", "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26, 'name': 'Bathroom'}"),
                Arguments.of(
                        "myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                            "{'temp': [22, 21, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    static Stream<Arguments> oneOfAFewMatchingMessagesWithTopic() {
        return Stream.of(
                Arguments.of(
                        "myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 'AS1000'}"),
                Arguments.of(
                        "+/groundfloor", "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor", "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26}"),
                Arguments.of(
                        "myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        "myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                            "{'temp': [22, 21, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }
}
