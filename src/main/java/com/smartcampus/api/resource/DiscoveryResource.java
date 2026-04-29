package com.smartcampus.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * DiscoveryResource provides the root endpoint of the API.
 * It returns general information about the API such as name,
 * version, admin contact, and available resource paths.
 * 
 * This helps clients understand how to navigate the API.
 * 
 * @author Chanumi
 */

// Defines the base path for this resource (root of the API)
@Path("/")

// Specifies that responses are returned in JSON format
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    /**
     * Handles GET requests to the root endpoint (/api/v1)
     * and returns API metadata and available resource links.
     *
     * @return a map containing API information and resource paths
     */
    @GET
    public Map<String, Object> getApiInfo() {

        // Main response map to hold API details
        Map<String, Object> response = new HashMap<>();

        // Map to hold available resource endpoints
        Map<String, String> resources = new HashMap<>();

        // Define resource paths for navigation
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        // Add general API information
        response.put("name", "Smart Campus API");
        response.put("version", "v1");
        response.put("adminContact", "admin@smartcampus.local");

        // Add resource links to the response
        response.put("resources", resources);

        // Return the final response as JSON
        return response;
    }
}