package model;

import java.time.LocalDateTime;

public class Enrollment {
    private int id;
    private int memberId;
    private int classId;
    private LocalDateTime enrolledAt;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}
