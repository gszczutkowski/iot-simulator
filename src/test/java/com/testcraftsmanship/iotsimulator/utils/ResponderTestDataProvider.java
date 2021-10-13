package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ResponderTestDataProvider {
    static Stream<Arguments> correctTopicWithMessagesAndResponses() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23}",
                        uuids.get(0) + "/myhome/groundfloor/+", "{'tmp': '{temperature}'}",
                        uuids.get(0) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'size': 50}, 'bathroom': {'tmp': 26, 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'size': 50}, 'bathroom': {'tmp': 26, 'size':10}}"),
                Arguments.of(
                        uuids.get(1) + "/myhome/groundfloor/livingroom", "{'tmp': 23, 'name': \"Tom's room\", 'size': 30, 'id': 'R03'}",
                        uuids.get(1) + "/myhome/groundfloor/+", "{'tmp': '{temperature}', 'name': '{name}', 'size': '{size}', 'id': 'R03'}",
                        uuids.get(1) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'name': '{name}', 'size': '{size}'}, 'bathroom': {'tmp': 26, 'name': \"Luisa's bathroom\", 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'name': \"Tom's room\", 'size': 30}, 'bathroom': {'tmp': 26, 'name': \"Luisa's bathroom\", 'size':10}}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'id': 'R007', 'data': [23, 30]}",
                        uuids.get(2) + "/myhome/groundfloor/+", "{'id': 'R007', 'data': ['{temperature}', '{size}']}",
                        uuids.get(2) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'size': '{size}'}, 'bathroom': {'tmp': 26, 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'size': 30}, 'bathroom': {'tmp': 26, 'size':10}}")
        );
    }

    static Stream<Arguments> correctTopicWithMessagesAndResponsesNoStrict() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23, 'name': 'My room'}",
                        uuids.get(0) + "/myhome/groundfloor/+", "{'tmp': '{temperature}'}",
                        uuids.get(0) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'size': 50}, 'bathroom': {'tmp': 26, 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'size': 50}, 'bathroom': {'tmp': 26, 'size':10}}"),
                Arguments.of(
                        uuids.get(1) + "/myhome/groundfloor/livingroom", "{'tmp': 23, 'name': \"Tom's room\", 'size': 30, 'id': 'R03'}",
                        uuids.get(1) + "/myhome/groundfloor/+", "{'tmp': '{temperature}', 'name': '{name}', 'size': '{size}'}",
                        uuids.get(1) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'name': '{name}', 'size': '{size}'}, 'bathroom': {'tmp': 26, 'name': \"Luisa's bathroom\", 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'name': \"Tom's room\", 'size': 30}, 'bathroom': {'tmp': 26, 'name': \"Luisa's bathroom\", 'size':10}}"),
                Arguments.of(
                        uuids.get(2) + "/myhome/groundfloor/livingroom", "{'id': 'R007', 'data': [23, 30]}",
                        uuids.get(2) + "/myhome/groundfloor/+", "{'data': ['{temperature}', '{size}']}",
                        uuids.get(2) + "/myhome/allrooms",
                        "{'livingroom': {'tmp': '{temperature}', 'size': '{size}'}, 'bathroom': {'tmp': 26, 'size':10}}",
                        "{'livingroom': {'tmp': 23, 'size': 30}, 'bathroom': {'tmp': 26, 'size':10}}")
        );
    }

    private static List<String> getUuids(int number) {
        return Arrays.stream(new String[number])
                .map(str -> UUID.randomUUID().toString()).collect(Collectors.toList());
    }
}
