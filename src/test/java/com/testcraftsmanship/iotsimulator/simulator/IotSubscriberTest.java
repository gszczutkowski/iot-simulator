package com.testcraftsmanship.iotsimulator.simulator;

import com.amazonaws.regions.Regions;
import com.testcraftsmanship.iotsimulator.IotSimulator;
import com.testcraftsmanship.iotsimulator.base.BaseAwsTest;
import com.testcraftsmanship.iotsimulator.iottype.subscriber.IotSubscriber;
import com.testcraftsmanship.iotsimulator.iottype.creator.subscriber.SubStateSelector;
import com.testcraftsmanship.iotsimulator.utils.SubscriberTestDataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static com.testcraftsmanship.iotsimulator.base.Constant.*;
import static org.assertj.core.api.Assertions.assertThat;

public class IotSubscriberTest extends BaseAwsTest implements SubscriberTestDataProvider {
    private IotSubscriber subscriber;

    @ParameterizedTest
    @MethodSource("strictMatchingMessagesWithTopic")
    public void subscriberShouldGetTheMessageWithStrictWhenSendToCorrectTopic(String topicWildcard, String pubTopic,
                                                                    String messageWildcard, String pubMessage) {
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

        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("strictNotMatchingMessagesWithTopic")
    public void subscriberShouldNotGetTheNotMatchingMessageWithStrictWhenSendToCorrectTopic(String topicWildcard, String pubTopic,
                                                                              String messageWildcard, String pubMessage) {
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

        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("strictNotMatchingMessagesWithTopic")
    public void subscriberShouldGetTheMatchingMessageWithStrictDisabledWhenSendToCorrectTopic(String topicWildcard, String pubTopic,
                                                                                            String messageWildcard, String pubMessage) {
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

        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("allMatchingMessagesWithTopic")
    public void subscriberShouldGetAllMatchingMessagesWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                             String pubTopic, String pubMessage1,  String pubMessage2) {
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

        publishWithAwsClient(pubTopic, pubMessage1);
        publishWithAwsClient(pubTopic, pubMessage2);

        assertThat(subscriber.allMatchingMessages()).containsExactlyInAnyOrderElementsOf(List.of(pubMessage1, pubMessage2));
        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("anyMatchingMessagesWithTopic")
    public void subscriberShouldGetAnyMatchingMessagesWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                             String pubTopic, String pubMessage1,  String pubMessage2) {
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

        publishWithAwsClient(pubTopic, pubMessage1);
        publishWithAwsClient(pubTopic, pubMessage2);

        assertThat(subscriber.allMatchingMessages()).containsExactlyInAnyOrderElementsOf(List.of(pubMessage1, pubMessage2));
        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("anyMatchingMessagesWithTopic")
    public void subscriberShouldNotGetAllWhenOnlyOneMatchingMessagesWhenSendToCorrectTopic(String topicWildcard, String messageWildcard,
                                                                             String pubTopic, String pubMessage1,  String pubMessage2) {
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

        publishWithAwsClient(pubTopic, pubMessage1);
        publishWithAwsClient(pubTopic, pubMessage2);

        assertThat(subscriber.allMatchingMessages().size()).isEqualTo(1);
        assertThat(subscriber.doesMatchingMessageReachedTheTopic()).isFalse();
    }

    @AfterEach
    public void tearDown() {
        subscriber.stop();
    }

    private SubStateSelector getSubscriber() {
        return new IotSimulator(MQTT_ENDPOINT,
                TESTED_CLIENT_ID,
                AWS_CREDENTIALS,
                Regions.EU_WEST_1.getName())
                .subscriber();
    }
}
