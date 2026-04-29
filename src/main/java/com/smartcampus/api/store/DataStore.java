package com.smartcampus.api.store;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * DataStore class acts as an in-memory storage for the application.
 * It stores rooms, sensors, and sensor readings using thread-safe collections.
 * This replaces the need for a database as per coursework requirements.
 * 
 * @author Chanumi
 */
public class DataStore {

    // Stores all rooms using room ID as the key
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    // Stores all sensors using sensor ID as the key
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

    // Stores sensor readings mapped by sensor ID
    public static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    /**
     * Static block initializes the DataStore with sample data.
     * This runs once when the class is loaded.
     */
    static {

        // Create sample rooms
        Room room1 = new Room("ROOM-101", "Lecture Hall A", 120, new CopyOnWriteArrayList<>());
        Room room2 = new Room("ROOM-102", "Computer Lab B", 40, new CopyOnWriteArrayList<>());

        // Add rooms to storage
        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);

        // Create sample sensors
        Sensor sensor1 = new Sensor("SEN-001", "Temperature", "ACTIVE", 25.5, "ROOM-101");
        Sensor sensor2 = new Sensor("SEN-002", "CO2", "MAINTENANCE", 410.0, "ROOM-102");

        // Add sensors to storage
        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);

        // Link sensors to their respective rooms
        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        // Initialize empty reading lists for each sensor
        sensorReadings.put(sensor1.getId(), new CopyOnWriteArrayList<>());
        sensorReadings.put(sensor2.getId(), new CopyOnWriteArrayList<>());
    }

    /**
     * Private constructor prevents instantiation of this class.
     * This ensures DataStore is used as a utility class only.
     */
    private DataStore() {
    }
}