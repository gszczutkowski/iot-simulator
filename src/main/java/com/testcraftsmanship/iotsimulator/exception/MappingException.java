package com.testcraftsmanship.iotsimulator.exception;

/**
 * Thrown to indicate that a json is inappropriate and can not be mapped to the mask.
 */
public class MappingException extends Exception {

    /**
     * Construct MappingException with specified message.
     *
     * @param message exception message
     */
    public MappingException(String message) {
        super(message);
    }
}
