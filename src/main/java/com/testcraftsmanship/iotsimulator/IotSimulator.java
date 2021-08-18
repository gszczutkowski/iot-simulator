package com.testcraftsmanship.iotsimulator;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.testcraftsmanship.iotsimulator.iottype.creator.publisher.IotPublisherCreator;
import com.testcraftsmanship.iotsimulator.iottype.creator.publisher.PubStateSelector;
import com.testcraftsmanship.iotsimulator.iottype.creator.responder.IotResponderCreator;
import com.testcraftsmanship.iotsimulator.iottype.creator.responder.ResStateSelector;
import com.testcraftsmanship.iotsimulator.iottype.creator.subscriber.IotSubscriberCreator;
import com.testcraftsmanship.iotsimulator.iottype.creator.subscriber.SubStateSelector;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLSocketFactory;
import java.security.KeyStore;

@Slf4j
public class IotSimulator {
    private final AWSIotMqttClient iotMqttClient;

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client certificate and private key are passed in through
     * the KeyStore argument. The key password protecting the private key in the KeyStore is also required.
     *
     * @param endpoint        in the form of <account-specific prefix>.iot.<aws-region>.amazonaws.com. The account-specific
     *                        prefix can be found on the AWS IoT console or by using the describe-endpoint command through the
     *                        AWS command line interface.
     * @param clientId        uniquely identify a MQTT connection. Two clients with the same client ID are not allowed to be
     *                        connected concurrently to a same endpoint.
     * @param certificateFile the key store containing the client X.509 certificate and private key. The KeyStore object can
     *                        be constructed using X.509 certificate file and private key file created on the AWS IoT console.
     *                        For more details, please refer to the README file of this SDK.
     * @param privateKeyFile  the key password protecting the private key in the keyStore argument.
     */
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

    public IotSimulator(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, int port) {
        iotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, socketFactory, port);
    }

    public PubStateSelector publisher() {
        log.debug("Created IoT device works in publisher mode");
        return new IotPublisherCreator(iotMqttClient);
    }

    public SubStateSelector subscriber() {
        log.debug("Created IoT device works in subscriber mode");
        return new IotSubscriberCreator(iotMqttClient);
    }

    public ResStateSelector responder() {
        log.debug("Created IoT device works in responder mode");
        return new IotResponderCreator(iotMqttClient);
    }
}
