package com.testcraftsmanship.iotsimulator.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.DEFAULT_MATCHING_RESTRICTIONS;
import static com.testcraftsmanship.iotsimulator.constant.GeneralConstants.DEFAULT_TIME_BETWEEN_RESPONSES;

@NoArgsConstructor
@Setter
@Getter
public class ResponderSettings {
    private int timeBetweenResponsesInSec = DEFAULT_TIME_BETWEEN_RESPONSES;
    private boolean isStrictMatching = DEFAULT_MATCHING_RESTRICTIONS;
}
