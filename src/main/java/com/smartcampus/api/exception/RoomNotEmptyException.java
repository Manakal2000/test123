package com.smartcampus.api.exception;

/**
 * Custom exception used when attempting to perform an operation
 * on a room that still contains related resources.
 * This ensures business rules are properly enforced.
 * 
 * @author Chanumi
 */
public class RoomNotEmptyException extends RuntimeException {

    /**
     * Constructor to create the exception with a custom message.
     * 
     * @param message error message describing the issue
     */
    public RoomNotEmptyException(String message) {
        super(message); // Call parent RuntimeException constructor
    }
}