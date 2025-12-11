package project.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private String id;
    private String name;
    private String surname;
    private int age; 
    private String role; // kid, parent, teacher
    private String password;

    private int points = 0;
    private int level = 1;

    private String classId;               // Kid's class
    private List<String> childrenIds;     // Parent's kids
    private List<String> classIds;        // Teacher's classes

    public User() {}

    public User(String name, String surname, String role, String password) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.password = password;

        this.childrenIds = new ArrayList<>();
        this.classIds = new ArrayList<>();
    }

    public static User createKid(String name, String surname, int age, String classId, String password) {
        User u = new User(name, surname, "kid", password);
        u.age = age;
        u.classId = classId;
        return u;
    }

    public static User createParent(String name, String surname, String password) {
        return new User(name, surname, "parent", password);
    }

    public static User createTeacher(String name, String surname, String password) {
        return new User(name, surname, "teacher", password);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public int getAge() { return age; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public int getPoints() { return points; }
    public int getLevel() { return level; }

    public String getClassId() { return classId; }
    public List<String> getChildrenIds() { return childrenIds; }
    public List<String> getClassIds() { return classIds; }

    public void addChild(String childId) {
        childrenIds.add(childId);
    }

    public void addClass(String classId) {
        classIds.add(classId);
    }

    public void addPoints(int p) {
        this.points += p;
        updateLevel();
    }

    private void updateLevel() {
        if (points < 50) level = 1;
        else if (points < 100) level = 2;
        else level = 3;
    }

    public String toString() {
        return "[" + role + "] " + name + " " + surname + 
               " | Points: " + points + " | Level: " + level;
    }
}
