package com.testcraftsmanship.iotsimulator.iottype.creator.base;

import com.testcraftsmanship.iotsimulator.item.IotMessage;
import com.testcraftsmanship.iotsimulator.item.SubscriptionData;

public interface SubscribeSetter {

    /**
     * Method sets exact topic on which expected message should be received. Topic should be in format e.g.:
     * /myhome/groundfloor/livingroom
     *
     * @param topic on which subscribed message should be received
     */
    default void setTopic(String topic) {
        if (topic.contains("#") || topic.contains("+")) {
            throw new IllegalArgumentException("Wildcards should not be passed as an expected topic. Use exact topic here.");
        }
        IotMessage iotMessage;
        if (getSubscriptionData() == null) {
            setSubscriptionData(new SubscriptionData());
        }
        if (getSubscriptionData().getTopicWildcard() == null) {
            getSubscriptionData().setTopicWildcard(topic);
        }
        if (getSubscriptionData().getIotMessage() == null) {
            iotMessage = new IotMessage();
        } else {
            iotMessage = getSubscriptionData().getIotMessage();
        }
        iotMessage.setTopic(topic);
        getSubscriptionData().setIotMessage(iotMessage);
    }

    /**
     * Sets the message which Iot Device is expecting to receive after subscribing to the given topic.
     * @param message which iot device is expecting
     */
    default void setMessage(String message) {
        IotMessage iotMessage;
        if (getSubscriptionData() == null) {
            setSubscriptionData(new SubscriptionData());
        }
        if (getSubscriptionData().getIotMessage() == null) {
            iotMessage = new IotMessage();
        } else {
            iotMessage = getSubscriptionData().getIotMessage();
        }
        iotMessage.setMessage(message);
        getSubscriptionData().setIotMessage(iotMessage);
    }

    SubscriptionData getSubscriptionData();

    void setSubscriptionData(SubscriptionData subscriptionData);
}
