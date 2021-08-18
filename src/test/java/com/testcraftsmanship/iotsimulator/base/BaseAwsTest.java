package com.testcraftsmanship.iotsimulator.base;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;

import static com.testcraftsmanship.iotsimulator.base.Constant.*;


/**
 * To be able to run the tests you have to have AWS CLI and user credentials configured. The default AWS user used for
 * sdk access should have added two policies in permissions: AmazonSSMReadOnlyAccess, AWSIoTFullAccess. There should be
 * also created parameter mqtt-client-endpoint in SSM Parameter Store in eu-west-1 region which as value contains your
 * MQTT client endpoint.
 */
public class BaseAwsTest {

    protected void publishWithAwsClient(String topic, String message) {
        AWSIotMqttClient mqttClient = getAWSIotMqttClient(TESTING_CLIENT_ID);
        try {
            mqttClient.connect();
            mqttClient.publish(topic, message);
            mqttClient.disconnect();
        } catch (AWSIotException e) {
            throw new IllegalStateException("Unable to publish message");
        }
    }

    private AWSIotMqttClient getAWSIotMqttClient(String clientId) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(AWS_CREDENTIALS);
        return new AWSIotMqttClient(MQTT_ENDPOINT,
                clientId,
                credentialsProvider,
                Regions.EU_WEST_1.getName());
    }
}
