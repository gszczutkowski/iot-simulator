package com.testcraftsmanship.iotsimulator.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class IotEvent {
    private String topic;
    private String message;
}
