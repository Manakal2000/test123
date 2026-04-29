package com.smartcampus.api.exception;

/**
 * Custom exception used when a sensor is not available to perform
 * a requested operation due to its current state.
 * This helps enforce system constraints and business rules.
 * 
 * @author Chanumi
 */
public class SensorUnavailableException extends RuntimeException {

    /**
     * Constructor to create the exception with a custom message.
     * 
     * @param message error message describing the issue
     */
    public SensorUnavailableException(String message) {
        super(message); // Call parent RuntimeException constructor
    }
}