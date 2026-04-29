package com.smartcampus.api.mapper;

import com.smartcampus.api.model.ApiError;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * GlobalExceptionMapper is a catch-all exception handler for the API.
 * It ensures that all unhandled exceptions are converted into proper
 * JSON responses instead of exposing internal server errors.
 * 
 * @author Chanumi
 */

// This annotation registers the mapper automatically in JAX-RS
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    /**
     * This method is called whenever an unhandled exception occurs.
     * It converts the exception into a standardized HTTP response.
     *
     * @param ex the exception that occurred
     * @return a Response object with proper status and JSON error body
     */
    @Override
    public Response toResponse(Throwable ex) {

        // If the exception is a WebApplicationException,
        // use its existing HTTP status code
        if (ex instanceof WebApplicationException webEx) {
            return Response.status(webEx.getResponse().getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ApiError(ex.getMessage())) // return error message as JSON
                    .build();
        }

        // For all other unexpected exceptions,
        // return a generic 500 Internal Server Error response
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError("An unexpected internal server error occurred."))
                .build();
    }
}