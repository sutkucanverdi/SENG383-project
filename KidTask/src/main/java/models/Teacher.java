package models;

import enums.UserRole;

public class Teacher extends User {
    public Teacher(String name) {
        this(null, name);
    }

    public Teacher(String id, String name) {
        super(id, name, UserRole.TEACHER);
    }
}

