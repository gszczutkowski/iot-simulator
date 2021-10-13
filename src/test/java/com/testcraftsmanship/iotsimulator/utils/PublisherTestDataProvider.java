package com.testcraftsmanship.iotsimulator.utils;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PublisherTestDataProvider {
    static Stream<Arguments> correctMessagesWithTopic() {
        List<String> uuids = getUuids(3);
        return Stream.of(
                Arguments.of(
                        uuids.get(0) + "/myhome/groundfloor/livingroom", "{'tmp': 23, 'id': 2}"),
                Arguments.of(
                        uuids.get(1) + "/myhome/groundfloor/livingroom/1/2/3", "{'id': 'AA', 'name': 'My room'}"),
                Arguments.of(
                        uuids.get(2) + "/my-home", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}")
        );
    }

    static List<String> getUuids(int number) {
        return Arrays.stream(new String[number])
                .map(str -> UUID.randomUUID().toString()).collect(Collectors.toList());
    }
}
