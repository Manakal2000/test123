package com.smartcampus.api.mapper;

import com.smartcampus.api.model.ApiError;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {

        if (ex instanceof WebApplicationException webEx) {
            return Response.status(webEx.getResponse().getStatus())
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ApiError(ex.getMessage()))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiError("An unexpected internal server error occurred."))
                .build();
    }
}