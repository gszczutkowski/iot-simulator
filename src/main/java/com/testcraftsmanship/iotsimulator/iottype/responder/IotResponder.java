package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;

import java.util.List;
import java.util.Map;

public class IotResponder extends IotDevice<IotResponder> {
    private final IotResponderTopic iotResponderTopic;

    public IotResponder(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData,
                        Map<String, List<String>> responseTopicsWithMessages, IotDeviceSettings settings) {
        super(iotMqttClient);
        this.iotResponderTopic = new IotResponderTopic(iotMqttClient, subscriptionData, responseTopicsWithMessages, settings);
    }

    @Override
    public IotResponder start() {
        super.start();
        try {
            getIotMqttClient().subscribe(iotResponderTopic, false);
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
