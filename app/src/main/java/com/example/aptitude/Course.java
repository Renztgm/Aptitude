package com.example.aptitude;

public class Course {
    private String id;
    private String name;
    private String description;

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    // Getter for ID
    public String getId() {
        return id;
    }
}

