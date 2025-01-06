package com.example.aptitude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Task {
    private String taskDescription;
    private String userId;
    private boolean isCompleted;
    private String date;
    private String id;  // Add id field for storing the Firestore document ID

    // No-argument constructor required for Firestore deserialization
    public Task() {
        // You can set default values here if needed
        this.taskDescription = "";
       // this.userId = "default_user";  // Default user ID (can be replaced with current user ID)
        this.isCompleted = false;
        this.date = " ";  // Default date if not provided
    }

    // Constructor with taskDescription, userId, isCompleted, and date
    public Task(String taskDescription, String userId, boolean isCompleted, String date) {
        this.taskDescription = taskDescription;
        this.userId = userId;
        this.isCompleted = isCompleted;
        this.date = date;
    }

    // Constructor for your specific use case
    public Task(String taskDescription) {
        this.taskDescription = taskDescription;
        this.userId = "default_user";  // Default user ID (can be replaced with current user ID)
        this.isCompleted = false;      // Default to not completed
        this.date = date;         // Default date if not provided
    }

    // Getters and setters
    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    private String getcurrentDate() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        int dayOfMonth = Integer.parseInt(sdf.format(today));

        String suffix = "th";
        if (dayOfMonth % 10 == 1 && dayOfMonth != 11) {
            suffix = "st";
        } else if (dayOfMonth % 10 == 2 && dayOfMonth != 12) {
            suffix = "nd";
        } else if (dayOfMonth % 10 == 3 && dayOfMonth != 13) {
            suffix = "rd";
        }

        // Return formatted date with suffix
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return dayOfMonth + suffix + " " + dateFormat.format(today);
    }


    // Getter for date
    public String getDate() {
        return date;
    }

    // Setter for date
    public void setDate(String date) {
        this.date = date;
    }

    // Firestore document ID getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Method to convert Task object to a Map for Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("taskDescription", taskDescription);
        taskMap.put("userId", userId);
        taskMap.put("isCompleted", isCompleted);
        taskMap.put("date", date);
        return taskMap;
    }
}
