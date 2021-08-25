package com.testcraftsmanship.iotsimulator.base;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;

import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static com.testcraftsmanship.iotsimulator.base.Constant.*;
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
        AWSIotMqttClient mqttClient = getAWSIotMqttClient(TESTING_CLIENT_ID);
        try {
            mqttClient.connect();
            for (String message : messages) {
                mqttClient.publish(topic, message);
            }
            mqttClient.disconnect();
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message");
        }
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

    private AWSIotMqttClient getAWSIotMqttClient(String clientId) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(AWS_CREDENTIALS);
        return new AWSIotMqttClient(MQTT_ENDPOINT,
                clientId,
                credentialsProvider,
                Regions.EU_WEST_1.getName());
    }
}
