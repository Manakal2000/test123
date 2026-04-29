package com.smartcampus.api.model;

/**
 * SensorReading class represents a single reading captured by a sensor.
 * It stores the reading value along with a timestamp.
 * 
 * @author Chanumi
 */
public class SensorReading {

    // Unique identifier for the reading
    private String id;

    // Timestamp representing when the reading was recorded (in milliseconds)
    private long timestamp;

    // Value recorded by the sensor
    private double value;

    /**
     * Default constructor required for frameworks (e.g., JSON serialization)
     */
    public SensorReading() {
    }

    /**
     * Parameterized constructor to initialize a SensorReading object
     * 
     * @param id reading ID
     * @param timestamp time when the reading was captured
     * @param value recorded value
     */
    public SensorReading(String id, long timestamp, double value) {
        this.id = id;
        this.timestamp = timestamp;
        this.value = value;
    }

    /**
     * Getter for reading ID
     * 
     * @return reading ID
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for reading ID
     * 
     * @param id new reading ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for timestamp
     * 
     * @return timestamp value
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Setter for timestamp
     * 
     * @param timestamp new timestamp value
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter for reading value
     * 
     * @return sensor reading value
     */
    public double getValue() {
        return value;
    }

    /**
     * Setter for reading value
     * 
     * @param value new reading value
     */
    public void setValue(double value) {
        this.value = value;
    }
}