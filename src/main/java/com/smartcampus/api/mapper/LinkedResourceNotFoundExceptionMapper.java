package com.smartcampus.api.mapper;

import com.smartcampus.api.exception.LinkedResourceNotFoundException;
import com.smartcampus.api.model.ApiError;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * This mapper handles LinkedResourceNotFoundException
 * and converts it into a proper HTTP response.
 * It ensures that clients receive a structured JSON error message.
 * 
 * @author Chanumi
 */

// Registers this class automatically as an exception handler
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    /**
     * Converts the exception into an HTTP response.
     *
     * @param ex the thrown LinkedResourceNotFoundException
     * @return Response with status 422 and JSON error body
     */
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {

        // Return HTTP 422 (Unprocessable Entity) with JSON error message
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError(ex.getMessage()))
                .build();
    }
}