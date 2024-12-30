package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CourseActivity extends AppCompatActivity {

    private MaterialButton notesButton, flashcardsButton, summarizeButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up the buttons
        notesButton = findViewById(R.id.button_1);
        flashcardsButton = findViewById(R.id.button_2);
        summarizeButton = findViewById(R.id.button_3);

        // Setting up back button functionality
        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed()); // Go back to the previous activity

        // Set up click listeners for each button
        notesButton.setOnClickListener(v -> openNotes());
        flashcardsButton.setOnClickListener(v -> openFlashcards());
        summarizeButton.setOnClickListener(v -> openSummarize());

        // Get the course ID from the Intent
        String courseId = getIntent().getStringExtra("courseId");
        Log.d("CourseActivity", "Received courseId: " + courseId);  // Log the received course ID

        if (courseId != null) {
            // Now you can fetch the course details using this courseId
            fetchCourseDetails(courseId);
        } else {
            Log.e("CourseActivity", "Course ID is null, cannot fetch course data.");
            // Optionally show an error message or return
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCourseDetails(String courseId) {
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get course details from Firestore
                        String courseName = documentSnapshot.getString("name");
                        String courseDescription = documentSnapshot.getString("description");

                        // Set course details to the UI
                        if (courseName != null) {
                            ((TextView) findViewById(R.id.course_name)).setText(courseName);
                        }
                        if (courseDescription != null) {
                            ((TextView) findViewById(R.id.course_description)).setText(courseDescription);
                        }
                        // Set courseId to the UI as well
                        ((TextView) findViewById(R.id.courseId)).setText(courseId);
                    } else {
                        Toast.makeText(CourseActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CourseActivity", "Failed to load course details: " + e.getMessage());
                    Toast.makeText(CourseActivity.this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                });
    }


    private void openNotes() {
        // Open the Notes section (e.g., using an Intent to start a new Activity)
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }

    private void openFlashcards() {
        // Open the Flash Cards section (e.g., using an Intent to start a new Activity)
        Intent intent = new Intent(this, FlashcardsActivity.class);
        startActivity(intent);
    }

    private void openSummarize() {
        // Open the Summarization section (e.g., using an Intent to start a new Activity)
        Intent intent = new Intent(this, SummarizeActivity.class);
        startActivity(intent);
    }
}
