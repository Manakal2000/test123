package com.smartcampus.api.resource;

import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.ApiError;
import com.smartcampus.api.model.ApiMessage;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * RoomResource handles all API operations related to Room management.
 * It provides endpoints to create, retrieve, and delete rooms.
 * 
 * @author Chanumi
 */

// Base path for this resource: /api/v1/rooms
@Path("/rooms")

// Specifies that responses will be returned in JSON format
@Produces(MediaType.APPLICATION_JSON)

// Specifies that requests must be in JSON format
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    /**
     * Handles GET /rooms
     * Returns a list of all rooms in the system.
     */
    @GET
    public Response getRooms() {

        // Retrieve all rooms from DataStore and convert to a list
        List<Room> rooms = new ArrayList<>(DataStore.rooms.values());

        // Return the list with HTTP 200 OK
        return Response.ok(rooms).build();
    }

    /**
     * Handles POST /rooms
     * Creates a new room after validating input data.
     */
    @POST
    public Response createRoom(Room room) {

        // Validate request body
        if (room == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Room body is required"))
                    .build();
        }

        // Validate room ID
        if (room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Room ID is required"))
                    .build();
        }

        // Validate room name
        if (room.getName() == null || room.getName().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Room name is required"))
                    .build();
        }

        // Validate capacity
        if (room.getCapacity() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiError("Room capacity must be greater than zero"))
                    .build();
        }

        // Check if room already exists
        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ApiError("Room already exists"))
                    .build();
        }

        // Ensure sensorIds list is not null
        if (room.getSensorIds() == null) {
            room.setSensorIds(new CopyOnWriteArrayList<>());
        }

        // Store the room in DataStore
        DataStore.rooms.put(room.getId(), room);

        // Return HTTP 201 Created with created room
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    /**
     * Handles GET /rooms/{roomId}
     * Retrieves a specific room by its ID.
     */
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {

        // Retrieve room from DataStore
        Room room = DataStore.rooms.get(roomId);

        // If room not found, return 404
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Room not found"))
                    .build();
        }

        // Return room with HTTP 200 OK
        return Response.ok(room).build();
    }

    /**
     * Handles DELETE /rooms/{roomId}
     * Deletes a room if it has no assigned sensors.
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {

        // Retrieve room from DataStore
        Room room = DataStore.rooms.get(roomId);

        // If room not found, return 404
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("Room not found"))
                    .build();
        }

        // Check if the room still has assigned sensors
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {

            // Throw custom exception to prevent deletion
            throw new RoomNotEmptyException(
                    "Room cannot be deleted because it still has assigned sensors."
            );
        }

        // Remove room from DataStore
        DataStore.rooms.remove(roomId);

        // Return success message
        return Response.ok(new ApiMessage("Room deleted successfully")).build();
    }
}