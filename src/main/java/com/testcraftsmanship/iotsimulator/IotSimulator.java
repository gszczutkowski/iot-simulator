package com.testcraftsmanship.iotsimulator;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.testcraftsmanship.iotsimulator.publisher.IotPublisherCreator;
import com.testcraftsmanship.iotsimulator.publisher.TopicSetter;

import java.security.KeyStore;

public class IotSimulator {
    private final AWSIotMqttClient iotMqttClient;

    public IotSimulator(String endpoint, String clientId, String certificateFile, String privateKeyFile) {
        SampleUtil.KeyStorePasswordPair pair = SampleUtil
                .getKeyStorePasswordPair(certificateFile, privateKeyFile);
        iotMqttClient = new AWSIotMqttClient(endpoint,
                clientId,
                pair.keyStore,
                pair.keyPassword);
    }

    public IotSimulator(String clientEndpoint, String clientId, Credentials credentials, String region) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(credentials);
        iotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, credentialsProvider, region);
    }

    public IotSimulator(String clientEndpoint, String clientId, KeyStore keyStore, String keyPassword) {
        iotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, keyStore, keyPassword);
    }

    public TopicSetter publisher() {
        return new IotPublisherCreator(iotMqttClient);
    }

}