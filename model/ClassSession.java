package model;

import java.sql.Timestamp;

/**
 * Represents a scheduled class session.
 */
public class ClassSession {
    private int id;
    private Timestamp time;
    private int instructorId;       // ID инструктора
    private String instructorName;
    private int capacity;

    public ClassSession(int id,
                        Timestamp time,
                        int instructorId,
                        String instructorName,
                        int capacity) {
        this.id = id;
        this.time = time;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }
    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getInstructorId() {
        return instructorId;
    }
    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }
    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
