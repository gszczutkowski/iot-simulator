package com.testcraftsmanship.iotsimulator.simulator;

import com.amazonaws.regions.Regions;
import com.testcraftsmanship.iotsimulator.IotSimulator;
import com.testcraftsmanship.iotsimulator.base.BaseAwsTest;
import com.testcraftsmanship.iotsimulator.iottype.subscriber.IotSubscriber;
import com.testcraftsmanship.iotsimulator.iottype.creator.subscriber.SubStateSelector;
import com.testcraftsmanship.iotsimulator.utils.SubscriberTestDataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static com.testcraftsmanship.iotsimulator.base.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

@Execution(ExecutionMode.CONCURRENT)
public class IotSubscriberTest extends BaseAwsTest implements SubscriberTestDataProvider {
    private IotSubscriber subscriber;

    @ParameterizedTest
    @MethodSource("strictMatchingMessagesWithTopic")
    public void subscriberShouldGetTheMessageWithStrictWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                              String pubTopic, String pubMessage) {
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage);

        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("strictNotMatchingMessagesWithTopic")
    public void subscriberShouldNotGetTheNotMatchingMessageWithStrictWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                                            String pubTopic, String pubMessage) {
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage);

        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("notMatchingTopics")
    public void subscriberShouldNotGetMessagesSendToWrongTopic(String subscribeTopic, String expectedTopic, String publishTopic) {
        final String mqttMessage = "{'id': 100, 'firstname': 'John', 'lastname': 'Smith'}";
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(subscribeTopic)
                .when()
                    .topicIs(expectedTopic)
                    .messageIs(mqttMessage)
                .then()
                    .anyMatch()
                .start();

        publishWithAwsClient(publishTopic, mqttMessage);

        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("strictDisabledMatchingMessagesWithTopic")
    public void subscriberShouldGetTheMatchingMessageWithStrictDisabledWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                                              String pubTopic, String pubMessage) {
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                    .strictMatchingDisabled()
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage);

        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isTrue();
    }


    @ParameterizedTest
    @MethodSource("strictDisabledNotMatchingMessagesWithTopic")
    public void subscriberShouldNoGetTheMatchingMessageWithStrictDisabledWhenNoMatchingMessagesSend(String topicWildcard, String messageWildcard,
                                                                                              String pubTopic, String pubMessage) {
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                    .strictMatchingDisabled()
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage);

        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("allMatchingMessagesWithTopic")
    public void subscriberShouldGetAllMatchingMessagesWithStrictDisabledWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                             String pubTopic, String pubMessage1,  String pubMessage2) throws TimeoutException {
        final int numberOfMatchingMessages = 2;
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                    .strictMatchingDisabled()
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage1, pubMessage2);

        waitUntil(() -> subscriber.allMatchingMessages().size() == numberOfMatchingMessages);
        assertThat(subscriber.allMatchingMessages()).containsExactlyInAnyOrderElementsOf(List.of(pubMessage1, pubMessage2));
        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("oneOfAFewMatchingMessagesWithTopic")
    public void subscriberShouldGetAnyMatchingMessagesWhenOnlyOneMatchingMessagesWasSendToCorrectTopic(String topicWildcard,
                                                                                String messageWildcard, String pubTopic,
                                                                                String correctMessage, String wrongMessage) throws TimeoutException {
        final int numberOfMatchingMessages = 1;
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                    .strictMatchingDisabled()
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .anyMatch()
                .start();

        publishWithAwsClient(pubTopic, correctMessage, wrongMessage);

        waitUntil(() -> subscriber.allMatchingMessages().size() == numberOfMatchingMessages);
        assertThat(subscriber.allMatchingMessages()).isEqualTo(List.of(correctMessage));
        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("oneOfAFewMatchingMessagesWithTopic")
    public void subscriberShouldNotGetAllWhenOnlyOneMatchingMessagesWasSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                             String pubTopic, String pubMessage1,  String pubMessage2) throws TimeoutException {
        final int numberOfAllMessages = 2;
        subscriber = getSubscriber()
                .given()
                    .subscribedTo(topicWildcard)
                    .strictMatchingDisabled()
                .when()
                    .topicIs(pubTopic)
                    .messageIs(messageWildcard)
                .then()
                    .allMatch()
                .start();

        publishWithAwsClient(pubTopic, pubMessage1, pubMessage2);

        waitUntil(() -> subscriber.allMessages().size() == numberOfAllMessages);
        assertThat(subscriber.allMatchingMessages()).isEqualTo(List.of(pubMessage1));
        assertThat(subscriber.doesExpectedMessagesReachedTheTopic()).isFalse();
    }

    @AfterEach
    public void tearDown() {
        subscriber.stop();
    }

    private SubStateSelector getSubscriber() {
        return new IotSimulator(MQTT_ENDPOINT,
                UUID.randomUUID().toString(),
                AWS_CREDENTIALS,
                Regions.EU_WEST_1.getName())
                .subscriber();
    }
}
