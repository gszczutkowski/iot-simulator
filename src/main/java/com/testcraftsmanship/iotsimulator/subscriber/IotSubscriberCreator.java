package com.testcraftsmanship.iotsimulator.subscriber;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IotSubscriberCreator implements SubscriberSetter {
    private final AWSIotMqttClient iotMqttClient;

    public IotSubscriberCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
    }

    public IotSubscriber topic(String topic) {
        log.info("Set subscriber to listen on topic {}", topic);
        return new IotSubscriber(iotMqttClient, topic);
    }

    @Override
    public IotSubscriber topicWithMessage(String topic, String message) {
        log.info("Set subscriber to listen on topic {}, with mask {}", topic, message);
        return new IotSubscriber(iotMqttClient, topic, message);
    }
}
