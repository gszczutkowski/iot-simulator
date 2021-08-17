package com.testcraftsmanship.iotsimulator.item;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionData {
    private String topicWildcard;
    private IotMessage iotMessage;
}
