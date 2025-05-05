package model;

import java.time.LocalDateTime;

public class GymClass {
    private int id;
    private LocalDateTime time;
    private int instructorId;
    private int capacity;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
