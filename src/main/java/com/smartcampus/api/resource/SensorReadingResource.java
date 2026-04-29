package com.smartcampus.api.resource;

import com.smartcampus.api.exception.SensorUnavailableException;
import com.smartcampus.api.model.ApiError;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;
import com.smartcampus.api.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SensorReadingResource handles all operations related to sensor readings.
 * It is a sub-resource that manages readings for a specific sensor.
 * 
 * @author Chanumi
 */

// Specifies that responses will be in JSON format
@Produces(MediaType.APPLICATION_JSON)

// Specifies that requests must be in JSON format
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Holds the sensor ID for which readings are managed
    private final String sensorId;

    /**
     * Constructor receives the sensor ID from the parent resource.
     * 
     * @param sensorId the ID of the sensor
     */
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * Handles GET /sensors/{id}/readings
     * Retrieves all readings for a specific sensor.
     */
    @GET
    public Response getReadings() {

        // Retrieve the sensor from DataStore
        Sensor sensor = DataStore.sensors.get(sensorId);

        // If sensor does not exist, return 404
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Sensor not found"))
                    .build();
        }

        // Get readings list for the sensor
        List<SensorReading> readings = DataStore.sensorReadings.get(sensorId);

        // If no readings exist, return an empty list
        if (readings == null) {
            readings = new ArrayList<>();
        }

        // Return readings with HTTP 200 OK
        return Response.ok(readings).build();
    }

    /**
     * Handles POST /sensors/{id}/readings
     * Adds a new reading to a specific sensor.
     */
    @POST
    public Response addReading(SensorReading reading) {

        // Retrieve the sensor from DataStore
        Sensor sensor = DataStore.sensors.get(sensorId);

        // If sensor does not exist, return 404
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Sensor not found"))
                    .build();
        }

        // Check if sensor is under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {

            // Throw exception if sensor cannot accept readings
            throw new SensorUnavailableException(
                    "Sensor is under maintenance and cannot accept readings."
            );
        }

        // Validate request body
        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Reading body is required"))
                    .build();
        }

        // Generate ID if not provided
        if (reading.getId() == null || reading.getId().isBlank()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Set current timestamp if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Ensure a list exists for this sensor
        DataStore.sensorReadings.putIfAbsent(sensorId, new CopyOnWriteArrayList<>());

        // Add the reading to the list
        DataStore.sensorReadings.get(sensorId).add(reading);

        // Update the sensor's current value with the new reading
        sensor.setCurrentValue(reading.getValue());

        // Return HTTP 201 Created with the new reading
        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}