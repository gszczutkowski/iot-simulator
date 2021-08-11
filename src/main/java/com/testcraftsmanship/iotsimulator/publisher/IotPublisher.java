package com.testcraftsmanship.iotsimulator.publisher;


import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.device.IotDevice;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazonaws.services.iot.client.AWSIotConnectionStatus.CONNECTED;

@Slf4j
public class IotPublisher extends IotDevice<IotPublisher> {
    private final Map<String, List<String>> topicsWithMessages;

    public IotPublisher(AWSIotMqttClient iotMqttClient, Map<String, List<String>> topicsWithMessages) {
        super(iotMqttClient);
        this.topicsWithMessages = topicsWithMessages;
    }

    @Override
    protected IotPublisher getThis() {
        return this;
    }

    public IotPublisher publish() {
        Optional<IotEvent> event = popMessageToPublish();
        if (event.isEmpty()) {
            log.info("There is no message to publish");
            return this;
        }
        startIotSimulatorWhenDisconnected();
        publishIotMessage(event.get());
        return this;
    }

    public IotPublisher publishAll() {
        startIotSimulatorWhenDisconnected();
        Optional<IotEvent> event = popMessageToPublish();
        while (event.isPresent()) {
            publishIotMessage(event.get());
            event = popMessageToPublish();
        }
        return this;
    }

    private Optional<IotEvent> popMessageToPublish() {
        if (topicsWithMessages == null || topicsWithMessages.isEmpty()) {
            return Optional.empty();
        }
        Map.Entry<String, List<String>> entry = topicsWithMessages.entrySet().iterator().next();
        String topic = entry.getKey();
        List<String> messages = entry.getValue();
        if (messages.size() == 1) {
            IotEvent result = new IotEvent(topic, messages.remove(0));
            topicsWithMessages.remove(topic);
            return Optional.of(result);
        } else if (messages.size() > 1) {
            IotEvent result = new IotEvent(topic, messages.remove(0));
            return Optional.of(result);
        } else {
            topicsWithMessages.remove(topic);
            return popMessageToPublish();
        }
    }

    private void publishIotMessage(@NonNull IotEvent message) {
        try {
            String payload = new JSONObject(message.getMessage()).toString();
            getIotMqttClient().publish(message.getTopic(), payload);
            log.info("Published message {} to topic {}", message.getMessage(), message.getTopic());
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message " + message.getMessage()
                    + " to topic " + message.getTopic());
        }
    }

    private void startIotSimulatorWhenDisconnected() {
        if (!CONNECTED.equals(getIotMqttClient().getConnectionStatus())) {
            start();
        }
    }
}






