package com.testcraftsmanship.iotsimulator.iottype.creator.subscriber;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.item.MatchingType;
import com.testcraftsmanship.iotsimulator.iottype.subscriber.IotSubscriber;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class IotSubscriberCreator implements SubSubscriberSetter, SubPreconditionSetter,
        SubActionSetter, SubStateSelector {
    private final AWSIotMqttClient iotMqttClient;
    private SubscriptionData subscriptionData;
    private final IotDeviceSettings settings = new IotDeviceSettings();

    public IotSubscriberCreator(AWSIotMqttClient iotMqttClient) {
        this.iotMqttClient = iotMqttClient;
    }

    @Override
    public SubPreconditionSetter given() {
        return this;
    }

    @Override
    public SubPreconditionSetter subscribedTo(String topicWildcard) {
        log.debug("Set subscriber to listen on topic {}", topicWildcard);
        this.subscriptionData = new SubscriptionData();
        subscriptionData.setTopicWildcard(topicWildcard);
        return this;
    }

    @Override
    public SubPreconditionSetter strictMatchingDisabled() {
        log.debug("Subscriber strict matching mechanism has been disabled");
        this.settings.setStrictMatching(false);
        return this;
    }

    @Override
    public SubSubscriberSetter when() {
        return this;
    }

    @Override
    public SubActionSetter then() {
        return this;
    }

    @Override
    public IotSubscriber allMatch() {
        log.debug("Set matching mechanism to match all");
        this.settings.setMatchingType(MatchingType.MATCH_ALL);
        return new IotSubscriber(iotMqttClient, subscriptionData, settings);
    }

    @Override
    public IotSubscriber anyMatch() {
        log.debug("Set matching mechanism to match any");
        this.settings.setMatchingType(MatchingType.MATCH_ANY);
        return new IotSubscriber(iotMqttClient, subscriptionData, settings);
    }

    @Override
    public SubscriptionData getSubscriptionData() {
        return subscriptionData;
    }

    @Override
    public void setSubscriptionData(SubscriptionData subscriptionData) {
        this.subscriptionData = subscriptionData;
    }
}
