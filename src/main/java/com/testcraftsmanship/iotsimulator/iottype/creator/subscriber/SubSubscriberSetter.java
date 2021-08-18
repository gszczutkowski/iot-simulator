package com.testcraftsmanship.iotsimulator.iottype.creator.subscriber;

import com.testcraftsmanship.iotsimulator.iottype.creator.base.SubscribeSetter;

public interface SubSubscriberSetter extends SubscribeSetter {
    default SubSubscriberSetter topicIs(String topic) {
        setTopic(topic);
        return this;
    }

    default SubSubscriberSetter messageIs(String message) {
        setMessage(message);
        return this;
    }
    SubActionSetter then();
}
