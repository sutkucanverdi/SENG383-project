package datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Constraint {
    private boolean fridayExamBlock = true;
    private boolean labAfterTheory = true;

    public Constraint() {}

    public boolean isFridayExamBlock() { return fridayExamBlock; }
    public void setFridayExamBlock(boolean fridayExamBlock) { this.fridayExamBlock = fridayExamBlock; }

    public boolean isLabAfterTheory() { return labAfterTheory; }
    public void setLabAfterTheory(boolean labAfterTheory) { this.labAfterTheory = labAfterTheory; }

    // Placeholder: checks schedule for any placement on Friday (returns list of conflict descriptions)
    public List<String> checkFridayBlock(Schedule schedule) {
        List<String> problems = new ArrayList<>();
        if (!fridayExamBlock) return problems;

        Map<String, Map<String, Course>> grid = schedule.getWeeklyGrid();
        Map<String, Course> friday = grid.get("Fri");
        if (friday != null) {
            for (Map.Entry<String, Course> e : friday.entrySet()) {
                if (e.getValue() != null) {
                    problems.add("Placement on Friday at " + e.getKey() + ": " + e.getValue());
                }
            }
        }
        return problems;
    }

    // Placeholder: ensure labs occur after theory; here we scan each course and if labHours>0 but no theory placement earlier, warn
    public List<String> checkLabAfterTheory(Schedule schedule) {
        List<String> problems = new ArrayList<>();
        // This is a placeholder — a real implementation would inspect the exact timeslots for each course.
        if (!labAfterTheory) return problems;

        for (Course c : schedule.getCourseList()) {
            if (c.getLabHours() > 0 && c.getTheoryHours() == 0) {
                problems.add("Course " + c.getCourseId() + " has lab but no theory block to place before lab.");
            }
        }
        return problems;
    }

    // check capacity <= 40
    public List<String> checkCapacity(Schedule schedule) {
        List<String> problems = new ArrayList<>();
        for (Course c : schedule.getCourseList()) {
            if (c.getLabHours() > 0 && c.getLabCapacity() > 40) {
                problems.add("Lab capacity exceeded for " + c.getCourseId() + " (" + c.getLabCapacity() + ")");
            }
        }
        return problems;
    }

    // Max 4 hours of theory per day per instructor (placeholder: counts theoryHours across courses grouped by lecturer)
    public List<String> checkInstructorLimit(Schedule schedule) {
        List<String> problems = new ArrayList<>();
        // naive aggregator
        Map<String, Integer> theoryPerInstructor = schedule.aggregateTheoryHoursByInstructor();
        for (Map.Entry<String, Integer> e : theoryPerInstructor.entrySet()) {
            if (e.getValue() > 4) {
                problems.add("Instructor " + e.getKey() + " exceeds 4 theory hours (total: " + e.getValue() + ")");
            }
        }
        return problems;
    }

    // Run all checks and return all problem messages
    public List<String> runAll(Schedule schedule) {
        List<String> all = new ArrayList<>();
        all.addAll(checkFridayBlock(schedule));
        all.addAll(checkLabAfterTheory(schedule));
        all.addAll(checkCapacity(schedule));
        all.addAll(checkInstructorLimit(schedule));
        return all;
    }
}