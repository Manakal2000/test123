package com.smartcampus.api.mapper;

import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.ApiError;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * This mapper handles SensorUnavailableException
 * and converts it into a proper HTTP response.
 * It ensures that clients receive a structured JSON error message
 * when a sensor is not available for an operation.
 * 
 * @author Chanumi
 */

// Registers this class automatically as an exception handler
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    /**
     * Converts the exception into an HTTP response.
     *
     * @param ex the thrown SensorUnavailableException
     * @return Response with status 403 (Forbidden) and JSON error body
     */
    @Override
    public Response toResponse(SensorUnavailableException ex) {

        // Return HTTP 403 Forbidden with JSON error message
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError(ex.getMessage()))
                .build();
    }
}