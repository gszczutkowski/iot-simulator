package com.testcraftsmanship.iotsimulator.exception;

/**
 * Thrown to indicate that there were problems with IoT connection.
 */
public class IotConfigurationException extends RuntimeException {

    /**
     * Construct IotConfigurationException with specified message.
     *
     * @param message exception message
     */
    public IotConfigurationException(String message) {
        super(message);
    }
}
