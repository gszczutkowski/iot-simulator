package com.testcraftsmanship.iotsimulator.iottype.creator.responder;

import com.testcraftsmanship.iotsimulator.iottype.creator.base.SubscribeSetter;

public interface ResSubscriberSetter extends SubscribeSetter {
    default ResSubscriberSetter topicIs(String topic) {
        setTopic(topic);
        return this;
    }

    default ResSubscriberSetter messageIs(String message) {
        setMessage(message);
        return this;
    }

    ResTopicSetter then();
}
