package model;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;
    private final String role;    // MEMBER, TRAINER или ADMIN

    public User(int id, String username, String passwordHash, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    // геттеры
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
}
