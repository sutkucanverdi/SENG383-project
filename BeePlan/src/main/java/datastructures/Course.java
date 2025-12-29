package datastructures;

public class Course {
    public enum Type { MANDATORY, ELECTIVE }

    private String courseId;
    private String name;
    private int theoryHours;
    private int labHours;
    private String lecturer;
    private Type type;
    private int labCapacity;

    public Course() {}

    public Course(String courseId, String name, int theoryHours, int labHours, String lecturer, Type type) {
        this.courseId = courseId;
        this.name = name;
        this.theoryHours = theoryHours;
        this.labHours = labHours;
        this.lecturer = lecturer;
        this.type = type;
        this.labCapacity = 0;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTheoryHours() { return theoryHours; }
    public void setTheoryHours(int theoryHours) { this.theoryHours = theoryHours; }

    public int getLabHours() { return labHours; }
    public void setLabHours(int labHours) { this.labHours = labHours; }

    public String getLecturer() { return lecturer; }
    public void setLecturer(String lecturer) { this.lecturer = lecturer; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getLabCapacity() { return labCapacity; }
    public void setLabCapacity(int labCapacity) { this.labCapacity = labCapacity; }

    @Override
    public String toString() {
        return courseId + " - " + name + " (" + type + ")";
    }
}