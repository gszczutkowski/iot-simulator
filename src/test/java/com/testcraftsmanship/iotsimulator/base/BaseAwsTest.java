package com.testcraftsmanship.iotsimulator.base;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testcraftsmanship.iotsimulator.IotSimulator;
import com.testcraftsmanship.iotsimulator.iottype.creator.publisher.PubStateSelector;
import com.testcraftsmanship.iotsimulator.iottype.creator.responder.ResStateSelector;
import com.testcraftsmanship.iotsimulator.iottype.creator.subscriber.SubStateSelector;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static com.testcraftsmanship.iotsimulator.base.Constant.AWS_CREDENTIALS;
import static com.testcraftsmanship.iotsimulator.base.Constant.MQTT_ENDPOINT;
import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.SUBSCRIBER_POLLING_VALUE_IN_SEC;


/**
 * To be able to run the tests you have to have AWS CLI and user credentials configured. The default AWS user used for
 * sdk access should have added two policies in permissions: AmazonSSMReadOnlyAccess, AWSIoTFullAccess. There should be
 * also created parameter mqtt-client-endpoint in SSM Parameter Store in eu-west-1 region which as value contains your
 * MQTT client endpoint.
 */
public class BaseAwsTest {
    private static final int DEFAULT_WAIT_TIME_IN_SECONDS = 5;

    protected void publishWithAwsClient(String topic, String... messages) {
        AWSIotMqttClient mqttClient = getAWSIotMqttClient(UUID.randomUUID().toString());
        try {
            mqttClient.connect();
            for (String message : messages) {
                mqttClient.publish(topic, new JSONObject(message).toString());
            }
            mqttClient.disconnect();
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message");
        }
    }

    protected AWSIotMqttClient subscribeWithAwsClient(String topic) {
        AWSIotMqttClient mqttClient = getAWSIotMqttClient(UUID.randomUUID().toString());
        try {
            SubscriptionTopic awsTopic = new SubscriptionTopic(topic);
            mqttClient.connect();
            mqttClient.subscribe(awsTopic);
            return mqttClient;
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message");
        }
    }

    protected PubStateSelector getPublisher() {
        return getSimulator().publisher();
    }

    protected SubStateSelector getSubscriber() {
        return getSimulator().subscriber();
    }

    protected ResStateSelector getResponder() {
        return getSimulator().responder();
    }

    protected void executeInBackground(Runnable toExecute) {
        final int requiredThread = 1;
        ExecutorService service = Executors.newFixedThreadPool(requiredThread);
        service.execute(toExecute);
    }

    @SneakyThrows
    protected JsonNode toJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(new JSONObject(json).toString());
    }

    @SneakyThrows
    protected List<JsonNode> toJson(List<String> jsons) {
        List<JsonNode> jsonNodes = new ArrayList<>();
        for (String json : jsons) {
            jsonNodes.add(toJson(json));
        }
        return jsonNodes;
    }

    protected void waitUntil(Supplier<Boolean> predicate) throws TimeoutException {
        waitUntil(predicate, DEFAULT_WAIT_TIME_IN_SECONDS);
    }

    protected void waitUntil(Supplier<Boolean> predicate, int timeoutInSeconds) throws TimeoutException {
        long timeout = Instant.now().getEpochSecond() + timeoutInSeconds;
        while (Instant.now().getEpochSecond() < timeout) {
            if (predicate.get()) {
                return;
            }
            try {
                TimeUnit.SECONDS.sleep(SUBSCRIBER_POLLING_VALUE_IN_SEC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new TimeoutException("Expected message didn't reach given topic or message does not match the mask");
    }

    protected void wait(int timeInSeconds) {
        try {
            TimeUnit.SECONDS.sleep(timeInSeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private AWSIotMqttClient getAWSIotMqttClient(String clientId) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(AWS_CREDENTIALS);
        return new AWSIotMqttClient(MQTT_ENDPOINT,
                clientId,
                credentialsProvider,
                Regions.EU_WEST_1.getName());
    }

    private IotSimulator getSimulator() {
        return new IotSimulator(MQTT_ENDPOINT,
                UUID.randomUUID().toString(),
                AWS_CREDENTIALS,
                Regions.EU_WEST_1.getName());
    }
}
