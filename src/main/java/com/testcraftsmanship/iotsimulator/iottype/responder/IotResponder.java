package com.testcraftsmanship.iotsimulator.iottype.responder;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;

import java.util.List;
import java.util.Map;

public class IotResponder extends IotDevice<IotResponder> {
    public IotResponder(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData,
                        Map<String, List<String>> responseTopicsWithMessages) {
        super(iotMqttClient);
    }

    @Override
    public IotResponder start() {
        super.start();
        //logic for publishing after receiving expected message
        return getThis();
    }

    @Override
    protected IotResponder getThis() {
        return this;
    }
}
