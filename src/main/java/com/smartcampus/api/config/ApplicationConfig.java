package com.smartcampus.api.config;

// Import JAX-RS annotation to define base URI path
import jakarta.ws.rs.ApplicationPath;

// Import Jersey configuration class
import org.glassfish.jersey.server.ResourceConfig;

/**
 * ApplicationConfig class is used to configure the JAX-RS application.
 * It defines the base API path and tells Jersey where to find resource classes.
 * 
 * @author Chanumi
 */

// This annotation defines the base URL path for all API endpoints
// All requests will start with: /api/v1
@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {

    /**
     * Constructor of the ApplicationConfig class.
     * This runs when the application starts.
     */
    public ApplicationConfig() {

        // This tells Jersey to scan the given package
        // and automatically register all resources, providers, and components
        // inside "com.smartcampus.api"
        packages("com.smartcampus.api"); 
    }
}