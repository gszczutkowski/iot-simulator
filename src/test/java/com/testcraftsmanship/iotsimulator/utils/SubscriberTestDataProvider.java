package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface SubscriberTestDataProvider {

    static Stream<Arguments> strictMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(6);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}",
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}"),
                Arguments.of(
                        "+/groundfloor/livingroom/" + uuids.get(1), "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom/" + uuids.get(1), "{'tmp': 23}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/+/livingroom", "{'name': 'my room'}",
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'name': 'my room'}"),
                Arguments.of(
                        uuids.get(3) + "/myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        uuids.get(3) + "/myhome/groundfloor/livingroom", "{'name': 'My room'}"),
                Arguments.of(
                        uuids.get(4) + "/myhome/#", "{'tmp': [10, 23]}",
                        uuids.get(4) + "/myhome/groundfloor/livingroom", "{'tmp': [10, 23]}"),
                Arguments.of(
                        uuids.get(5) + "/myhome/#", "{'id': 'A5', 'tmp': [10, 23]}",
                        uuids.get(5) + "/myhome/groundfloor/livingroom", "{'tmp': [10, 23], 'id': 'A5'}")
        );
    }

    static Stream<Arguments> strictNotMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(4);
        return Stream.of(
                Arguments.of(
                        "+/groundfloor/livingroom/" + uuids.get(0), "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom/" + uuids.get(0), "{'tmp': 'ID'}"),
                Arguments.of(
                        uuids.get(1) + "/myhome/+/livingroom", "{'name': 'my room', 'id' = 2}",
                        uuids.get(1) + "/myhome/groundfloor/livingroom", "{'name': 'my room', 'id' = 3}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'name': 'My room 77', 'id' = 3"),
                Arguments.of(
                        uuids.get(3) + "/myhome/#", "{'tmp': 23}",
                        uuids.get(3) + "/myhome/groundfloor/livingroom", "{'tmp': [23]}")
        );
    }

    static Stream<Arguments> notMatchingTopics() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of("/+/groundfloor/livingroom/" + uuids.get(0), "groundfloor/livingroom/" + uuids.get(0), "/myhome/groundfloor/livingroom/" + uuids.get(0)),
                Arguments.of(uuids.get(1) + "/myhome/groundfloor/livingroom", uuids.get(1) + "/myhome/groundfloor", uuids.get(1) + "/myhome/groundfloor/livingroom"),
                Arguments.of(uuids.get(2) + "/myhome/groundfloor/+", uuids.get(2) + "/myhome/groundfloor/livingroom", uuids.get(2) + "/myhome/groundfloor/bathroom")
        );
    }

    static Stream<Arguments> strictDisabledMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(6);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}",
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23, 'id': 2}"),
                Arguments.of(
                        "+/groundfloor/livingroom/" + uuids.get(1), "{'tmp': '{[0-9]+}'}",
                        "myhome/groundfloor/livingroom/" + uuids.get(1), "{'tmp': 23, 'id': 2}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'id': 'AA', 'name': 'My room'}"),
                Arguments.of(
                        uuids.get(3) + "/myhome/#", "{'a': {'b': {'c' : 21, 'd':'{[A-Z]+}'}}}",
                        uuids.get(3) + "/myhome/groundfloor/livingroom", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}"),
                Arguments.of(
                        uuids.get(4) + "/myhome/#", "{'a': {'d': {'e': '{[0-9]+}'}}}",
                        uuids.get(4) + "/myhome/groundfloor/livingroom", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}"),
                Arguments.of(
                        uuids.get(5) + "/myhome/#", "{'a': {'b': {'c' : 21}, 'd': {'e': '{[0-9]+}'}}}",
                        uuids.get(5) + "/myhome/groundfloor/livingroom", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}")
        );
    }

    static Stream<Arguments> strictDisabledNotMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/+", "{'name': '{[A-Za-z ]+}'}",
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'id': 'AA', 'name': 12}"),
                Arguments.of(
                        uuids.get(1) + "/myhome/#", "{'tmp': [10, 23]}",
                        uuids.get(1) + "/myhome/groundfloor/livingroom", "{'tmp': [10]}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/#", "{'tmp': [10, 23]}",
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'tmp': [1023]}")
        );
    }

    static Stream<Arguments> allMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 1000}"),
                Arguments.of(
                        "+/groundfloor/" + uuids.get(1), "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor/" +  uuids.get(1), "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26, 'name': 'Bathroom'}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        uuids.get(2) + "/myhome/groundfloor", "{'name': 'groundfloor', 'livingroom': {'temp': 22}, 'temp': [21, 22, 20, 24]}",
                            "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    static Stream<Arguments> oneOfAFewMatchingMessagesWithTopic() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/+", "{'tmp': '{[0-9]+}'}",
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}", "{'tmp': 'AS1000'}"),
                Arguments.of(
                        "+/groundfloor" + uuids.get(1), "{'tmp': '{[0-9]+}', 'name': '{[a-zA-Z]+}'}",
                        "myhome/groundfloor" + uuids.get(1), "{'tmp': 23, 'name': 'Livingroom'}", "{'tmp': 26}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/#", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                        uuids.get(2) + "/myhome/groundfloor", "{'temp': [21, 22, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}",
                            "{'temp': [22, 21, 20, 24], 'name': 'groundfloor', 'livingroom': {'temp': 22}}")
        );
    }

    private static List<String> getUuids(int number) {
        return Arrays.stream(new String[number])
                .map(str -> UUID.randomUUID().toString()).collect(Collectors.toList());
    }
}
