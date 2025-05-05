package model;

import java.sql.Timestamp;

public class ClassSession {
    private final int id;
    private final Timestamp time;
    private final String instructorName;
    private final int capacity;

    public ClassSession(int id, Timestamp time, String instructorName, int capacity) {
        this.id = id;
        this.time = time;
        this.instructorName = instructorName;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTime() {
        return time;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public int getCapacity() {
        return capacity;
    }
}
