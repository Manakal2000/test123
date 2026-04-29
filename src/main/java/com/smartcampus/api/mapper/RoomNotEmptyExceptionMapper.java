package com.smartcampus.api.mapper;

import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.ApiError;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * This mapper handles RoomNotEmptyException
 * and converts it into a proper HTTP response.
 * It ensures that clients receive a clear JSON error message
 * when a room cannot be modified due to existing linked resources.
 * 
 * @author Chanumi
 */

// Registers this class automatically as an exception handler
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    /**
     * Converts the exception into an HTTP response.
     *
     * @param ex the thrown RoomNotEmptyException
     * @return Response with status 409 (Conflict) and JSON error body
     */
    @Override
    public Response toResponse(RoomNotEmptyException ex) {

        // Return HTTP 409 Conflict with JSON error message
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError(ex.getMessage()))
                .build();
    }
}