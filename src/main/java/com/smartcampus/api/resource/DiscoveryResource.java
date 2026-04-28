package com.smartcampus.api.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> getApiInfo() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> resources = new HashMap<>();

        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        response.put("name", "Smart Campus API");
        response.put("version", "v1");
        response.put("adminContact", "admin@smartcampus.local");
        response.put("resources", resources);

        return response;
    }
}