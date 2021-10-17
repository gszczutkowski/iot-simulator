package com.testcraftsmanship.iotsimulator.iottype.subscriber;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.testcraftsmanship.iotsimulator.iottype.generic.IotDevice;
import com.testcraftsmanship.iotsimulator.exception.IotConfigurationException;
import com.testcraftsmanship.iotsimulator.item.IotDeviceSettings;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.SUBSCRIBER_POLLING_VALUE_IN_SEC;

/**
 * Class responsible for subscribing to given topic and waiting for expected message or message mask
 */
public class IotSubscriber extends IotDevice<IotSubscriber> {
    private final SubscribedTopic subscribedTopic;

    /**
     * Instantiating IotSubscriber responsible for waiting for messages on given topic and validation whether message
     * is as expected.
     *
     * @param iotMqttClient AWSIotMqttClient which will be responsible for publishing messages
     * @param subscriptionData containing subscription topic and expected message/mask
     * @param settings settings of the responder: if exact subscription message is required, whether all messages should
     *                 match expected or any, delay of response is omitted
     */
    public IotSubscriber(AWSIotMqttClient iotMqttClient, SubscriptionData subscriptionData, IotDeviceSettings settings) {
        super(iotMqttClient);
        this.subscribedTopic = new SubscribedTopic(subscriptionData, settings);
    }

    /**
     * Runs IotSubscriber which means that starts connection and subscribes to the given topic.
     *
     * @return Iot Subscriber object which just has been started
     */
    @Override
    public IotSubscriber start() {
        super.start();
        try {
            getIotMqttClient().subscribe(subscribedTopic, false);
        } catch (AWSIotException e) {
            throw new IotConfigurationException("Unable to subscribe to given topic");
        }
        return getThis();
    }

    /**
     * Wait with given number of seconds for message matching the predefined mask on predefined topic. If matching
     * message is received in given time then it is returned. If no message has been received in given time then
     * TimeoutException is thrown.
     *
     * @param timeoutInSeconds value in seconds describing how long to wait for the expected message
     * @return received message
     * @throws TimeoutException when message has not been received in given time
     */
    public String waitForMatchingMessage(int timeoutInSeconds) throws TimeoutException {
        long timeout = Instant.now().getEpochSecond() + timeoutInSeconds;
        while (Instant.now().getEpochSecond() < timeout) {
            Optional<String> message = subscribedTopic.popReceivedMessage();
            if (message.isPresent()) {
                return message.get();
            }
            try {
                TimeUnit.SECONDS.sleep(SUBSCRIBER_POLLING_VALUE_IN_SEC);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new TimeoutException("Expected message didn't reach given topic or message does not match the mask");
    }

    /**
     * Check whether message matching the mask has been received to given topic. When the method returns true then
     * it returns true in the next run only if expected message is received once again.
     *
     * @return true if message has been received, false if message has not been received
     */
    public boolean doesExpectedMessagesReachedTheTopic() {
        return subscribedTopic.receivedExpectedMessagesMatchingMask();
    }

    /**
     * Extracts all messages matching the mask on subscribed topic which were received from subscription moment.
     *
     * @return all matching messages
     */
    public List<String> allMatchingMessages() {
        return subscribedTopic.matchingReceivedMessages();
    }

    /**
     * Extracts all messages on subscribed topic which were received from subscription moment.
     *
     * @return all messages
     */
    public List<String> allMessages() {
        return subscribedTopic.allReceivedMessages();
    }

    @Override
    protected IotSubscriber getThis() {
        return this;
    }
}
