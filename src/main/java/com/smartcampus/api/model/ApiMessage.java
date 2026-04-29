package com.smartcampus.api.model;

/**
 * ApiMessage class is used to represent successful responses in the API.
 * It provides a simple structure to return messages in JSON format.
 * 
 * @author Chanumi
 */
public class ApiMessage {

    // This field stores the message
    private String message;

    /**
     * Default constructor required for frameworks (e.g., JSON serialization)
     */
    public ApiMessage() {
    }

    /**
     * Constructor to create an ApiMessage with a specific message
     * 
     * @param message message to be returned
     */
    public ApiMessage(String message) {
        this.message = message;
    }

    /**
     * Getter method to retrieve the message
     * 
     * @return message text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method to update the message
     * 
     * @param message new message text
     */
    public void setMessage(String message) {
        this.message = message;
    }
}