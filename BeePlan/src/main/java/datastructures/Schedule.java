package datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Schedule {
    private String id;
    // weeklyGrid: day -> timeslot -> Course
    private Map<String, Map<String, Course>> weeklyGrid;
    private List<Course> courseList;

    public Schedule() {
        this.weeklyGrid = new HashMap<>();
        this.courseList = new ArrayList<>();
        // initialize weekday maps for Mon-Fri
        weeklyGrid.put("Mon", new HashMap<>());
        weeklyGrid.put("Tue", new HashMap<>());
        weeklyGrid.put("Wed", new HashMap<>());
        weeklyGrid.put("Thu", new HashMap<>());
        weeklyGrid.put("Fri", new HashMap<>());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Map<String, Map<String, Course>> getWeeklyGrid() { return weeklyGrid; }
    public void setWeeklyGrid(Map<String, Map<String, Course>> weeklyGrid) { this.weeklyGrid = weeklyGrid; }

    public List<Course> getCourseList() { return courseList; }
    public void setCourseList(List<Course> courseList) { this.courseList = courseList; }

    public void addCourse(Course c) { this.courseList.add(c); }

    // naive placement: try to place each course into first available timeslot on first available day
    public void placeCoursesAuto(List<String> timeslots) {
        for (Course c : courseList) {
            boolean placed = false;
            for (String day : new String[] {"Mon","Tue","Wed","Thu","Fri"}) {
                Map<String, Course> dayMap = weeklyGrid.get(day);
                for (String slot : timeslots) {
                    if (!dayMap.containsKey(slot) || dayMap.get(slot) == null) {
                        dayMap.put(slot, c);
                        placed = true;
                        break;
                    }
                }
                if (placed) break;
            }
        }
    }

    // detect simple conflicts: same timeslot/day assigned more than once — with this model we only allow one course per slot so conflict if null-check fails
    public List<String> detectConflicts() {
        List<String> problems = new ArrayList<>();
        // placeholder: no multiple assignment support; if some marker indicates conflict, we'd add messages
        // Keep this as extension point
        return problems;
    }

    // helper to aggregate theory hours by instructor (naive, not day-sliced)
    public Map<String, Integer> aggregateTheoryHoursByInstructor() {
        Map<String, Integer> agg = new HashMap<>();
        for (Course c : courseList) {
            String inst = c.getLecturer() == null ? "UNKNOWN" : c.getLecturer();
            agg.put(inst, agg.getOrDefault(inst, 0) + c.getTheoryHours());
        }
        return agg;
    }
}