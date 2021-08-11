package com.testcraftsmanship.iotsimulator.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class IotEvent {
    private String topic;
    private String message;
}
