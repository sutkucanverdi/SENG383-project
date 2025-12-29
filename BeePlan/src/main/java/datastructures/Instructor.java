package datastructures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instructor {
    // availability map: day -> list of available timeslot strings
    private String name;
    private Map<String, List<String>> availability;

    public Instructor() {
        this.availability = new HashMap<>();
    }

    public Instructor(String name, Map<String, List<String>> availability) {
        this.name = name;
        this.availability = availability == null ? new HashMap<>() : availability;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, List<String>> getAvailability() { return availability; }
    public void setAvailability(Map<String, List<String>> availability) { this.availability = availability; }

    public boolean isAvailable(String day, String timeslot) {
        List<String> slots = availability.get(day);
        return slots != null && slots.contains(timeslot);
    }

    @Override
    public String toString() {
        return "Instructor{" + name + "}";
    }
}