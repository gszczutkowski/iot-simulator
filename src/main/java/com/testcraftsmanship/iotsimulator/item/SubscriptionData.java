package com.testcraftsmanship.iotsimulator.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionData {
    private String topicWildcard;
    private IotEvent iotEvent;
}
