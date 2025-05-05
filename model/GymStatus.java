package model;

public class GymStatus {
    private final boolean isActive;

    public GymStatus(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }
}
