package com.testcraftsmanship.iotsimulator.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IotMessage {
    private String topic;
    private String message;
}
