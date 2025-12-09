package models;

import enums.UserRole;

public class Parent extends User {
    public Parent(String name) {
        this(null, name);
    }

    public Parent(String id, String name) {
        super(id, name, UserRole.PARENT);
    }
}

