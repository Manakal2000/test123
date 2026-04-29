package com.smartcampus.api.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Room class represents a physical room in the smart campus system.
 * It stores room details and the list of associated sensor IDs.
 * 
 * @author Chanumi
 */
public class Room {

    // Unique identifier of the room
    private String id;

    // Name of the room
    private String name;

    // Maximum capacity of the room
    private int capacity;

    // List of sensor IDs assigned to this room
    // CopyOnWriteArrayList is used for thread-safe operations
    private List<String> sensorIds = new CopyOnWriteArrayList<>();

    /**
     * Default constructor required for frameworks (e.g., JSON serialization)
     */
    public Room() {
    }

    /**
     * Parameterized constructor to initialize a Room object
     * 
     * @param id unique room ID
     * @param name room name
     * @param capacity maximum capacity
     * @param sensorIds list of sensor IDs
     */
    public Room(String id, String name, int capacity, List<String> sensorIds) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;

        // If sensorIds is null, initialize with an empty thread-safe list
        this.sensorIds = sensorIds != null ? sensorIds : new CopyOnWriteArrayList<>();
    }

    /**
     * Getter for room ID
     * 
     * @return room ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for room ID
     * 
     * @param id new room ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for room name
     * 
     * @return room name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for room name
     * 
     * @param name new room name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for room capacity
     * 
     * @return capacity value
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Setter for room capacity
     * 
     * @param capacity new capacity value
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Getter for sensor ID list
     * 
     * @return list of sensor IDs
     */
    public List<String> getSensorIds() {
        return sensorIds;
    }

    /**
     * Setter for sensor ID list
     * Ensures a thread-safe list is always used
     * 
     * @param sensorIds list of sensor IDs
     */
    public void setSensorIds(List<String> sensorIds) {

        // If null is passed, replace with empty thread-safe list
        this.sensorIds = sensorIds != null ? sensorIds : new CopyOnWriteArrayList<>();
    }
}