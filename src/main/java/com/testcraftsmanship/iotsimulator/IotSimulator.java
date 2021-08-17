package com.testcraftsmanship.iotsimulator;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.testcraftsmanship.iotsimulator.iottype.publisher.IotPublisherCreator;
import com.testcraftsmanship.iotsimulator.iottype.publisher.PubTopicSetter;
import com.testcraftsmanship.iotsimulator.iottype.responder.IotResponderCreator;
import com.testcraftsmanship.iotsimulator.iottype.responder.StateSelector;
import com.testcraftsmanship.iotsimulator.iottype.subscriber.IotSubscriberCreator;
import com.testcraftsmanship.iotsimulator.iottype.subscriber.SubscriberSetter;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyStore;

@Slf4j
public class IotSimulator {
    private final AWSIotMqttClient iotMqttClient;

    public IotSimulator(String endpoint, String clientId, String certificateFile, String privateKeyFile) {
        SampleUtil.KeyStorePasswordPair pair = SampleUtil
                .getKeyStorePasswordPair(certificateFile, privateKeyFile);
        iotMqttClient = new AWSIotMqttClient(endpoint,
                clientId,
                pair.keyStore,
                pair.keyPassword);
        log.debug("Created IoT device with id {} connected to {}", clientId, endpoint);
    }

    public IotSimulator(String endpoint, String clientId, Credentials credentials, String region) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(credentials);
        iotMqttClient = new AWSIotMqttClient(endpoint, clientId, credentialsProvider, region);
        log.debug("Created IoT device with id {} connected to {}", clientId, endpoint);
    }

    public IotSimulator(String endpoint, String clientId, KeyStore keyStore, String keyPassword) {
        iotMqttClient = new AWSIotMqttClient(endpoint, clientId, keyStore, keyPassword);
        log.debug("Created IoT device with id {} connected to {}", clientId, endpoint);
    }

    public PubTopicSetter publisher() {
        log.debug("Created IoT device works in publisher mode");
        return new IotPublisherCreator(iotMqttClient);
    }

    public SubscriberSetter subscriber() {
        log.debug("Created IoT device works in subscriber mode");
        return new IotSubscriberCreator(iotMqttClient);
    }

    public StateSelector responder() {
        log.debug("Created IoT device works in responder mode");
        return new IotResponderCreator(iotMqttClient);
    }
}
