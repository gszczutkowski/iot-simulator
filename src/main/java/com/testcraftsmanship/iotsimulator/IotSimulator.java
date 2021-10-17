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

    /**
     * Instantiates a new client using Secure WebSocket and AWS SigV4 authentication. AWS IAM credentials, are required
     * for signing the request. Credentials can be permanent ones associated with IAM users or temporary ones generated
     * via the AWS Cognito service.
     *
     * @param endpoint    the client endpoint in the form of <account-specific-prefix>.iot.<region>.amazonaws.com .
     *                    The account-specific prefix can be found on the AWS IoT console or by using the describe-endpoint
     *                    command through the AWS command line interface.
     * @param clientId    the client ID uniquely identify a MQTT connection. Two clients with the same client ID are not
     *                    allowed to be connected concurrently to a same endpoint.
     * @param credentials AWS credential including the access key ID and secret access key
     * @param region      the AWS region
     */
    public IotSimulator(String endpoint, String clientId, Credentials credentials, String region) {
        StaticCredentialsProvider credentialsProvider = new StaticCredentialsProvider(credentials);
        iotMqttClient = new AWSIotMqttClient(endpoint, clientId, credentialsProvider, region);
        log.debug("Created IoT device with id {} connected to {}", clientId, endpoint);
    }

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client certificate and private key are passed in through
     * the KeyStore argument. The key password protecting the private key in the KeyStore is also required.
     *
     * @param endpoint    the client endpoint in the form of <account-specific prefix>.iot.<aws-region>.amazonaws.com.
     *                    The account-specific prefix can be found on the AWS IoT console or by using the describe-endpoint
     *                    command through the AWS command line interface.
     * @param clientId    the client ID uniquely identify a MQTT connection. Two clients with the same client ID are not
     *                    allowed to be connected concurrently to a same endpoint.
     * @param keyStore    the key store containing the client X.509 certificate and private key. The KeyStore object can be
     *                    constructed using X.509 certificate file and private key file created on the AWS IoT console.
     *                    For more details, please refer to the README file of this SDK.
     * @param keyPassword the key password protecting the private key in the keyStore argument.
     */
    public IotSimulator(String endpoint, String clientId, KeyStore keyStore, String keyPassword) {
        iotMqttClient = new AWSIotMqttClient(endpoint, clientId, keyStore, keyPassword);
        log.debug("Created IoT device with id {} connected to {}", clientId, endpoint);
    }

    /**
     * Instantiates a new client using TLS 1.2 mutual authentication. Client certificate and private key should be used
     * to initialize the KeyManager of the socketFactory.
     *
     * @param clientEndpoint the client endpoint in the form of <account-specific prefix>.iot.<aws-region>.amazonaws.com.
     *                       The account-specific prefix can be found on the AWS IoT console or by using the describe-endpoint
     *                       command through the AWS command line interface.
     * @param clientId       the client ID uniquely identify a MQTT connection. Two clients with the same client ID are not
     *                       allowed to be connected concurrently to a same endpoint.
     * @param socketFactory  A socketFactory instantiated with a Keystore containing the client X.509 certificate and private
     *                       key, and a Truststore containing trusted Certificate Authorities(CAs).
     * @param port           The socket port to use.
     */
    public IotSimulator(String clientEndpoint, String clientId, SSLSocketFactory socketFactory, int port) {
        iotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, socketFactory, port);
    }

    /**
     * Returns PubStateSelector which is responsible for configuration of Iot simulator to work as message publisher.
     * It is not possible to subscribe on topics in this mode.
     *
     * @return object responsible for publisher configuration
     */
    public PubStateSelector publisher() {
        log.debug("Created IoT device works in publisher mode");
        return new IotPublisherCreator(iotMqttClient);
    }

    /**
     * Returns SubStateSelector which is responsible for configuration of Iot simulator to work as subscriber.
     * It is not possible to publish messages in this mode.
     *
     * @return object responsible for subscriber configuration
     */
    public SubStateSelector subscriber() {
        log.debug("Created IoT device works in subscriber mode");
        return new IotSubscriberCreator(iotMqttClient);
    }

    /**
     * Returns SubStateSelector which is responsible for configuration of Iot simulator to work as message responder.
     * In this mode Iot simulator is waiting for receiving expected message and respond with different message after that.
     *
     * @return object responsible for responder configuration
     */
    public ResStateSelector responder() {
        log.debug("Created IoT device works in responder mode");
        return new IotResponderCreator(iotMqttClient);
    }
}
