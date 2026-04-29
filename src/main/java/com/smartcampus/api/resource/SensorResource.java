package com.smartcampus.api.resource;

import com.smartcampus.api.exception.LinkedResourceNotFoundException;
import com.smartcampus.api.model.ApiError;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * SensorResource handles all API operations related to sensors.
 * It supports creating, retrieving, and filtering sensors,
 * and also provides access to sensor readings as a sub-resource.
 * 
 * @author Chanumi
 */

// Base path: /api/v1/sensors
@Path("/sensors")

// Responses are returned in JSON format
@Produces(MediaType.APPLICATION_JSON)

// Requests must be in JSON format
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    /**
     * Handles GET /sensors
     * Optionally filters sensors by type using query parameter.
     *
     * @param type optional sensor type filter
     * @return list of sensors
     */
    @GET
    public Response getSensors(@QueryParam("type") String type) {

        // Retrieve all sensors
        List<Sensor> list = new ArrayList<>(DataStore.sensors.values());

        // Apply filtering if type parameter is provided
        if (type != null && !type.isBlank()) {
            list = list.stream()
                    .filter(sensor -> sensor.getType() != null
                            && sensor.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }

        // Return sensor list with HTTP 200 OK
        return Response.ok(list).build();
    }

    /**
     * Handles GET /sensors/{sensorId}
     * Retrieves a specific sensor by ID.
     *
     * @param sensorId sensor ID
     * @return sensor object or 404 if not found
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {

        // Retrieve sensor from DataStore
        Sensor sensor = DataStore.sensors.get(sensorId);

        // If not found, return 404
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Sensor not found"))
                    .build();
        }

        // Return sensor with HTTP 200 OK
        return Response.ok(sensor).build();
    }

    /**
     * Handles POST /sensors
     * Creates a new sensor after validating input data.
     *
     * @param sensor sensor object from request body
     * @return created sensor or error response
     */
    @POST
    public Response createSensor(Sensor sensor) {

        // Validate request body
        if (sensor == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Sensor body is required"))
                    .build();
        }

        // Validate sensor ID
        if (sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Sensor ID required"))
                    .build();
        }

        // Validate sensor type
        if (sensor.getType() == null || sensor.getType().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Sensor type required"))
                    .build();
        }

        // Validate sensor status
        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Sensor status required"))
                    .build();
        }

        // Validate associated room ID
        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Sensor roomId required"))
                    .build();
        }

        // Check if sensor already exists
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiError("Sensor already exists"))
                    .build();
        }

        // Check if the referenced room exists
        Room room = DataStore.rooms.get(sensor.getRoomId());

        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "The specified roomId does not exist."
            );
        }

        // Store sensor in DataStore
        DataStore.sensors.put(sensor.getId(), sensor);

        // Ensure sensor list exists in room
        if (room.getSensorIds() == null) {
            room.setSensorIds(new CopyOnWriteArrayList<>());
        }

        // Add sensor ID to the room
        room.getSensorIds().add(sensor.getId());

        // Return HTTP 201 Created
        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    /**
     * Sub-resource locator for sensor readings.
     * Handles paths: /sensors/{sensorId}/readings
     *
     * @param sensorId sensor ID
     * @return SensorReadingResource instance
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {

        // Return sub-resource instance for managing readings
        return new SensorReadingResource(sensorId);
    }
}