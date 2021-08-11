package com.testcraftsmanship.iotsimulator.iottype.responder;

import java.util.List;
import java.util.Map;

public interface ResponderTopicSetter {
    ResponderMsgSetter publishingTopicIs(String topic);

    IotResponder publishingTopicsWithMessages(Map<String, List<String>> topicsWitMessages);
}
