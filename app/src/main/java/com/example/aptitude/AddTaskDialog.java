package com.example.aptitude;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.Map;

public class AddTaskDialog {

    private Activity activity;
    private ArrayList<Task> taskList;
    private TaskAdapter taskAdapter;
    private CollectionReference tasksCollection;

    public AddTaskDialog(Activity activity, ArrayList<Task> taskList, TaskAdapter taskAdapter, CollectionReference tasksCollection) {
        this.activity = activity;
        this.taskList = taskList;
        this.taskAdapter = taskAdapter;
        this.tasksCollection = tasksCollection;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add New Task");

        final EditText input = new EditText(activity);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskDescription = input.getText().toString().trim();
            if (!taskDescription.isEmpty()) {
                Task newTask = new Task(taskDescription);

                // Convert Task to Map before adding to Firestore
                Map<String, Object> taskMap = newTask.toMap();

                // Ensure tasksCollection is not null
                if (tasksCollection != null) {
                    tasksCollection.add(taskMap)
                            .addOnSuccessListener(documentReference -> {
                                newTask.setId(documentReference.getId()); // Set the id after the task is added
                                Toast.makeText(activity, "Task added", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(activity, "Error adding task", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(activity, "Firestore collection is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "Please enter a task description", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}
