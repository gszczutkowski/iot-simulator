package com.testcraftsmanship.iotsimulator.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SubscriptionData {
    private String subscriptionTopic;
    private String subscriptionTopicMatcher;
    private String subscriptionMessageMatcher;
}
