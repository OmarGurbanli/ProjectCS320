package model;

public class Instructor {
    private final int id;
    private final String name;

    public Instructor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return getName() + " (" + getId() + ")";
    }

}
