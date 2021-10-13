package com.testcraftsmanship.iotsimulator.simulator;

import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.base.BaseAwsTest;
import com.testcraftsmanship.iotsimulator.base.SubscriptionTopic;
import com.testcraftsmanship.iotsimulator.iottype.publisher.IotPublisher;
import com.testcraftsmanship.iotsimulator.utils.PublisherTestDataProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IotPublisherTest extends BaseAwsTest implements PublisherTestDataProvider {
    private static final int MAX_MQTT_MESSAGE_PROCESSING_TIME = 2;
    private IotPublisher publisher;
    private AWSIotMqttClient awsSubscriber;

    @ParameterizedTest
    @MethodSource("correctMessagesWithTopic")
    public void messagesShouldBePublishedToCorrectTopic(String topic, String message) {
        publisher = getPublisher()
                .given()
                    .topic(topic)
                    .message(message);

        awsSubscriber = subscribeWithAwsClient(topic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        publisher.publish();
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(topic);
        List<String> messages = subscriptionTopic.popMessages();

        assertThat(messages.size()).isEqualTo(1);
        assertThat(toJson(messages.get(0))).isEqualTo(toJson(message));
    }

    @Test
    public void onlyOneMessageShouldBePublishedWithPublishOption() {
        String uuid = UUID.randomUUID().toString();
        String topic = uuid + "/myhome/groundfloor/livingroom";
        List<String> messages = List.of("{'tmp': 23, 'id': 2}",
                "{'id': 'AA', 'name': 'My room'}", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}");
        Map<String, List<String>> messagesWithTopics = Map.of(topic, messages);

        publisher = getPublisher()
                .given()
                        .topicsWithMessages(messagesWithTopics);

        awsSubscriber = subscribeWithAwsClient(topic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        publisher.publish();
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(topic);
        List<String> actualMessages = subscriptionTopic.popMessages();

        assertThat(actualMessages.size()).isEqualTo(1);
        assertThat(toJson(actualMessages.get(0))).isEqualTo(toJson(messages.get(0)));
    }

    @Test
    public void allMessagesShouldBePublishedWithPublishAllOption() {
        String uuid = UUID.randomUUID().toString();
        String topic = uuid + "/myhome/groundfloor/livingroom";
        List<String> messages = List.of("{'tmp': 23, 'id': 2}",
                "{'id': 'AA', 'name': 'My room'}", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}");
        Map<String, List<String>> messagesWithTopics = Map.of(topic, messages);

        publisher = getPublisher()
                .given()
                .topicsWithMessages(messagesWithTopics);

        awsSubscriber = subscribeWithAwsClient(topic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        publisher.publishAll();
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(topic);
        List<String> actualMessages = subscriptionTopic.popMessages();

        assertThat(toJson(actualMessages)).containsExactlyInAnyOrderElementsOf(toJson(messages));
    }

    @Test
    public void messagesShouldBeSendWithDelayWhenItIsSet() {
        String uuid = UUID.randomUUID().toString();
        String topic = uuid + "/myhome/groundfloor/livingroom";
        List<String> messages = List.of("{'tmp': 23, 'id': 2}",
                "{'id': 'AA', 'name': 'My room'}", "{'a': {'b': {'c' : 21, 'd':'AWX'}, 'd': {'e': 0}}}");
        Map<String, List<String>> messagesWithTopics = Map.of(topic, messages);

        publisher = getPublisher()
                .given()
                    .publishingDelay(MAX_MQTT_MESSAGE_PROCESSING_TIME + 1)
                    .topicsWithMessages(messagesWithTopics);

        awsSubscriber = subscribeWithAwsClient(topic);
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        executeInBackground(()-> publisher.publishAll());
        wait(MAX_MQTT_MESSAGE_PROCESSING_TIME);

        SubscriptionTopic subscriptionTopic = (SubscriptionTopic)awsSubscriber.getSubscriptions().get(topic);
        List<String> actualMessages = subscriptionTopic.popMessages();

        assertThat(actualMessages.size()).isEqualTo(1);
        assertThat(toJson(actualMessages.get(0))).isEqualTo(toJson(messages.get(0)));
    }

    @SneakyThrows
    @AfterEach
    public void tearDown() {
        publisher.stop();
        awsSubscriber.disconnect();
    }
}
