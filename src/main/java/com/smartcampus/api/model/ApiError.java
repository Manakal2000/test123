package com.smartcampus.api.model;

/**
 * ApiError class is used to represent error responses in the API.
 * It provides a simple structure to return error messages in JSON format.
 * 
 * @author Chanumi
 */
public class ApiError {

    // This field stores the error message
    private String error;

    /**
     * Default constructor required for frameworks (e.g., JSON serialization)
     */
    public ApiError() {
    }

    /**
     * Constructor to create an ApiError with a specific message
     * 
     * @param error error message to be returned
     */
    public ApiError(String error) {
        this.error = error;
    }

    /**
     * Getter method to retrieve the error message
     * 
     * @return error message
     */
    public String getError() {
        return error;
    }

    /**
     * Setter method to update the error message
     * 
     * @param error new error message
     */
    public void setError(String error) {
        this.error = error;
    }
}