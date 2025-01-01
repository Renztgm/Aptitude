package com.example.aptitude;

public class Course {
    private String id;
    private String name;
    private String description;

    // Constructor that accepts id, name, and description
    public Course(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getter for id
    public String getId() {
        return id;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }
}


