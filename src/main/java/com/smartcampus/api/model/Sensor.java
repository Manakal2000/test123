package com.smartcampus.api.model;

/**
 * Sensor class represents a device installed within a room.
 * It stores sensor details such as type, status, and current value,
 * along with the room it is assigned to.
 * 
 * @author Chanumi
 */
public class Sensor {

    // Unique identifier of the sensor
    private String id;

    // Type of the sensor (e.g., Temperature, CO2, Occupancy)
    private String type;

    // Current status of the sensor (e.g., ACTIVE, MAINTENANCE, OFFLINE)
    private String status;

    // Latest value recorded by the sensor
    private double currentValue;

    // ID of the room where the sensor is located
    private String roomId;

    /**
     * Default constructor required for frameworks (e.g., JSON serialization)
     */
    public Sensor() {
    }

    /**
     * Parameterized constructor to initialize a Sensor object
     * 
     * @param id sensor ID
     * @param type sensor type
     * @param status sensor status
     * @param currentValue latest recorded value
     * @param roomId associated room ID
     */
    public Sensor(String id, String type, String status, double currentValue, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    /**
     * Getter for sensor ID
     * 
     * @return sensor ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for sensor ID
     * 
     * @param id new sensor ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for sensor type
     * 
     * @return sensor type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for sensor type
     * 
     * @param type new sensor type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for sensor status
     * 
     * @return sensor status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter for sensor status
     * 
     * @param status new sensor status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Getter for current sensor value
     * 
     * @return current value
     */
    public double getCurrentValue() {
        return currentValue;
    }

    /**
     * Setter for current sensor value
     * 
     * @param currentValue new value
     */
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    /**
     * Getter for room ID
     * 
     * @return associated room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Setter for room ID
     * 
     * @param roomId new room ID
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}