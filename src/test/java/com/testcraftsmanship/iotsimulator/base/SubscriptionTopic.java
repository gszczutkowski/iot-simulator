package com.testcraftsmanship.iotsimulator.base;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotTopic;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionTopic extends AWSIotTopic {
    private List<String> messages;

    public SubscriptionTopic(String topic) {
        super(topic);
        messages = new ArrayList<>();
    }

    @Override
    public synchronized void onMessage(AWSIotMessage message) {
        messages.add(message.getStringPayload());
    }

    public synchronized List<String> popMessages() {
        List<String> currentMessages = messages;
        messages = new ArrayList<>();
        return currentMessages;
    }
}
