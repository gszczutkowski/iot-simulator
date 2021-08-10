package com.testcraftsmanship.iotsimulator.device;

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
            log.info("IoT simulator started");
        } catch (AWSIotException e) {
            log.warn("Exception thrown while starting IoT simulator");
        }
        return getThis();
    }

    public T stop() {
        try {
            iotMqttClient.disconnect();
            log.info("IoT simulator stopped");
        } catch (AWSIotException e) {
            log.warn("Exception thrown while stopping IoT simulator");
        }
        return getThis();
    }

    protected abstract T getThis();
}
