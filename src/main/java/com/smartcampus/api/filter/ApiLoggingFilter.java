package com.smartcampus.api.filter;

// Import interfaces for request and response filtering
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

// Import Provider annotation to register the filter automatically
import jakarta.ws.rs.ext.Provider;

// Import IOException for method signatures
import java.io.IOException;

// Import Logger for logging messages
import java.util.logging.Logger;

/**
 * ApiLoggingFilter is used to log all incoming requests and outgoing responses.
 * It helps monitor API activity and improves debugging and observability.
 * 
 * @author Chanumi
 */

// This annotation tells JAX-RS to automatically detect and register this filter
@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    // Logger instance used to print log messages
    private static final Logger LOGGER = Logger.getLogger(ApiLoggingFilter.class.getName());

    /**
     * This method is executed before the request reaches the resource method.
     * It logs the HTTP method and request URI.
     *
     * @param requestContext contains request information
     * @throws IOException if an input-output error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Log incoming request method (GET, POST, etc.) and full URI
        LOGGER.info("Incoming Request: " +
                requestContext.getMethod() + " " +
                requestContext.getUriInfo().getRequestUri());
    }

    /**
     * This method is executed after the resource method is processed.
     * It logs the HTTP response status code.
     *
     * @param requestContext contains request information
     * @param responseContext contains response information
     * @throws IOException if an input-output error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Log outgoing response status (e.g., 200, 404, 500)
        LOGGER.info("Outgoing Response: HTTP " + responseContext.getStatus());
    }
}