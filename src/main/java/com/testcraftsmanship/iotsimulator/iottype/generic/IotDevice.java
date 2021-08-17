package com.testcraftsmanship.iotsimulator.iottype.generic;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public abstract class IotDevice<T extends IotDevice> {
    @Getter
    private final AWSIotMqttClient iotMqttClient;

    public T start() {
        try {
            iotMqttClient.connect();
            log.debug("IoT simulator started");
        } catch (AWSIotException e) {
            log.error("Exception thrown while starting IoT simulator");
        }
        return getThis();
    }

    public T stop() {
        try {
            iotMqttClient.disconnect();
            log.debug("IoT simulator stopped");
        } catch (AWSIotException e) {
            log.error("Exception thrown while stopping IoT simulator");
        }
        return getThis();
    }

    protected abstract T getThis();
}
