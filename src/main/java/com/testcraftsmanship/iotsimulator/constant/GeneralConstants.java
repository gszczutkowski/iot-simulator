package com.testcraftsmanship.iotsimulator.constant;

import com.testcraftsmanship.iotsimulator.item.MatchingType;

public final class GeneralConstants {
    private GeneralConstants() {
    }

    public static final int SUBSCRIBER_POLLING_VALUE_IN_SEC = 1;
    public static final int DEFAULT_TIME_BETWEEN_RESPONSES = 0;
    public static final int DEFAULT_TIME_BETWEEN_PUBLISHES = 0;
    public static final int RESPONDER_POOLING_TIME_IN_MILLIS = 500;
    public static final boolean DEFAULT_MATCHING_RESTRICTIONS = true;
    public static final MatchingType DEFAULT_MATCHING_TYPE = MatchingType.MATCH_ANY;
}
