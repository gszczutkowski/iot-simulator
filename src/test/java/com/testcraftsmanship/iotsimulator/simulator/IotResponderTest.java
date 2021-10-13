package com.testcraftsmanship.iotsimulator.simulator;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.base.BaseAwsTest;
import com.testcraftsmanship.iotsimulator.base.SubscriptionTopic;
import com.testcraftsmanship.iotsimulator.iottype.responder.IotResponder;
import com.testcraftsmanship.iotsimulator.utils.ResponderTestDataProvider;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IotResponderTest extends BaseAwsTest implements ResponderTestDataProvider {
    private static final int MAX_MQTT_MESSAGE_PROCESSING_TIME = 2;
    private static final String NOT_MATCHING_MESSAGE = "{'uuid': '6ecef02b-bc3a-48df-936a-91091082d025', 'info': [21, 33, 17]}";
    private IotResponder responder;
    private AWSIotMqttClient awsSubscriber;

    @ParameterizedTest
    @MethodSource("correctTopicWithMessagesAndResponses")
    public void responderShouldRespondToCorrectTopicWhenGetTheMessageWithStrict(String publishTopic, String publishMessage,
                                                                              String subscribeTopic, String subscribeMessageMask,
                                                                              String responseTopic, String responseMessageMask,
                                                                              String responseMessage) {

        responder = getResponder()
                .given()
                    .subscribedTo(subscribeTopic)
                .when()
                    .topicIs(publishTopic)
                    .messageIs(subscribeMessageMask)
                .then()
                    .publishingTo(responseTopic)
                    .publishingMessage(responseMessageMask)
                .start();
        awsSubscriber = subscribeWithAwsClient(responseTopic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        publishWithAwsClient(publishTopic, NOT_MATCHING_MESSAGE);
        publishWithAwsClient(publishTopic, new JSONObject(publishMessage).toString());

        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(responseTopic);
        List<String> messages = subscriptionTopic.popMessages();

        assertThat(messages.size()).isEqualTo(1);
        assertThat(toJson(messages.get(0))).isEqualTo(toJson(responseMessage));
    }

    @ParameterizedTest
    @MethodSource("correctTopicWithMessagesAndResponsesNoStrict")
    public void responderShouldRespondToCorrectTopicWhenGetTheMessageWithNoStrict(String publishTopic, String publishMessage,
                                                                              String subscribeTopic, String subscribeMessageMask,
                                                                              String responseTopic, String responseMessageMask,
                                                                              String responseMessage) {
        publishWithAwsClient(publishTopic, NOT_MATCHING_MESSAGE);
        responder = getResponder()
                .given()
                    .strictMatchingDisabled()
                    .subscribedTo(subscribeTopic)
                .when()
                    .topicIs(publishTopic)
                    .messageIs(subscribeMessageMask)
                .then()
                    .publishingTo(responseTopic)
                    .publishingMessage(responseMessageMask)
                .start();
        awsSubscriber = subscribeWithAwsClient(responseTopic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        publishWithAwsClient(publishTopic, new JSONObject(publishMessage).toString());

        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(responseTopic);
        List<String> messages = subscriptionTopic.popMessages();

        assertThat(messages.size()).isEqualTo(1);
        assertThat(toJson(messages.get(0))).isEqualTo(toJson(responseMessage));
    }

    @SneakyThrows
    @AfterEach
    public void tearDown() {
        responder.stop();
        awsSubscriber.disconnect();
    }
}
