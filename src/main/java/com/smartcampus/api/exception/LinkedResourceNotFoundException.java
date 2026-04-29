package com.smartcampus.api.exception;

/**
 * Custom exception used when a required linked resource is not found.
 * This helps to handle specific business logic errors in a clear way.
 * 
 * @author Chanumi
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    /**
     * Constructor to create the exception with a custom message.
     * 
     * @param message error message describing the issue
     */
    public LinkedResourceNotFoundException(String message) {
        super(message); // Call parent RuntimeException constructor
    }
}