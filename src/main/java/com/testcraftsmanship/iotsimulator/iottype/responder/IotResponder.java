package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;
import com.testcraftsmanship.iotsimulator.item.ResponderSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;

import java.util.List;
import java.util.Map;

public class IotResponder extends IotDevice<IotResponder> {
    private final ResponderTopic responderTopic;

    public IotResponder(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData,
                        Map<String, List<String>> responseTopicsWithMessages, ResponderSettings settings) {
        super(iotMqttClient);
        this.responderTopic = new ResponderTopic(iotMqttClient, subscriptionData, responseTopicsWithMessages, settings);
    }

    @Override
    public IotResponder start() {
        super.start();
        try {
            getIotMqttClient().subscribe(responderTopic, false);
        } catch (AWSIotException e) {
            throw new IotConfigurationException("Unable to subscribe to given topic");
        }
        return getThis();
    }

    @Override
    protected IotResponder getThis() {
        return this;
    }
}
