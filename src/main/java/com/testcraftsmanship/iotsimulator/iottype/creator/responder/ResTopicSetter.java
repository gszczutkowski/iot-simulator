package com.testcraftsmanship.iotsimulator.iottype.creator.responder;

import com.testcraftsmanship.iotsimulator.iottype.responder.IotResponder;

import java.util.List;
import java.util.Map;

public interface ResTopicSetter {
    ResMsgSetter publishingTo(String topic);
    IotResponder publishingTopicsWithMessages(Map<String, List<String>> topicsWitMessages);
}
