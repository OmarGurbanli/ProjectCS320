package model;

import java.time.LocalDateTime;

public class Class {
    private int classId;
    private LocalDateTime time;
    private int instructorId;
    private int capacity;

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
