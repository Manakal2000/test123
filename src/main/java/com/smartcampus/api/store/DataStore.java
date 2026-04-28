package com.smartcampus.api.store;

import com.smartcampus.api.model.Room;
import com.smartcampus.api.model.Sensor;
import com.smartcampus.api.model.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataStore {

    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    static {
        Room room1 = new Room("ROOM-101", "Lecture Hall A", 120, new CopyOnWriteArrayList<>());
        Room room2 = new Room("ROOM-102", "Computer Lab B", 40, new CopyOnWriteArrayList<>());

        rooms.put(room1.getId(), room1);
        rooms.put(room2.getId(), room2);

        Sensor sensor1 = new Sensor("SEN-001", "Temperature", "ACTIVE", 25.5, "ROOM-101");
        Sensor sensor2 = new Sensor("SEN-002", "CO2", "MAINTENANCE", 410.0, "ROOM-102");

        sensors.put(sensor1.getId(), sensor1);
        sensors.put(sensor2.getId(), sensor2);

        room1.getSensorIds().add(sensor1.getId());
        room2.getSensorIds().add(sensor2.getId());

        sensorReadings.put(sensor1.getId(), new CopyOnWriteArrayList<>());
        sensorReadings.put(sensor2.getId(), new CopyOnWriteArrayList<>());
    }

    private DataStore() {
    }
}