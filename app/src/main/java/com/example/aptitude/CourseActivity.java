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
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);


        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed()); // Go back to the previous activity

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set up the buttons
        notesButton = findViewById(R.id.button_1);
        flashcardsButton = findViewById(R.id.button_2);
        summarizeButton = findViewById(R.id.button_3);

        // Setting up back button functionality

        // Set up click listeners for each button
        notesButton.setOnClickListener(v -> openNotes());
        flashcardsButton.setOnClickListener(v -> openFlashcards());
        summarizeButton.setOnClickListener(v -> openSummarize());

        // Get the course ID from the Intent
        courseId = getIntent().getStringExtra("courseId");
        Log.d("CourseActivity", "Received courseId: " + courseId);  // Log the received course ID

        if (courseId != null) {
            // Now you can fetch the course details using this courseId
            fetchCourses(courseId);
        } else {
            Log.e("CourseActivity", "Course ID is null, cannot fetch course data.");
            // Optionally show an error message or return
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
        }

        // Add the edit button functionality
        findViewById(R.id.edit).setOnClickListener(v -> {
            EditCourseDialog dialog = new EditCourseDialog(
                    this,
                    courseId,
                    firestore,
                    this::refreshCourseDetails // Pass the refresh method as a callback
            );
            dialog.show();
        });

        findViewById(R.id.delete).setOnClickListener(v -> deleteCourse());
    }


    private void fetchCourses(String courseId) {
        Log.d("CourseActivity", "Fetching details for courseId: " + courseId);
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("CourseActivity", "Course found: " + courseId);
                        // Get course details from Firestore
                        String courseName = documentSnapshot.getString("name");
                        String courseDescription = documentSnapshot.getString("description");

                        // Log the retrieved values
                        Log.d("CourseActivity", "Course Name: " + courseName);
                        Log.d("CourseActivity", "Course Description: " + courseDescription);

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
                        Log.e("CourseActivity", "Course document does not exist.");
                        Toast.makeText(CourseActivity.this, "Course not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CourseActivity", "Failed to load course details: " + e.getMessage());
                    Toast.makeText(CourseActivity.this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to delete a course
    private void deleteCourse() {
        if (courseId == null) {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CourseActivity", "Deleting course with ID: " + courseId);

        // Get the user's Firestore document and delete the course
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("CourseActivity", "Course deleted successfully.");
                    Toast.makeText(this, "Course deleted successfully", Toast.LENGTH_SHORT).show();

                    // Notify Tab1Fragment to refresh its data
                    Tab1Fragment tab1Fragment = (Tab1Fragment) getSupportFragmentManager().findFragmentByTag("f0");
                    if (tab1Fragment != null) {
                        tab1Fragment.refreshCourseData();
                    }

                    // Close the current activity and return to the previous screen
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("CourseActivity", "Failed to delete course: " + e.getMessage());
                    Toast.makeText(this, "Failed to delete course", Toast.LENGTH_SHORT).show();
                });
    }

    private void refreshCourseDetails() {
        String courseId = getIntent().getStringExtra("courseId");
        if (courseId != null) {
            fetchCourses(courseId); // Refresh the data by re-fetching course details

            // Find Tab1Fragment and refresh its data
            Tab1Fragment tab1Fragment = (Tab1Fragment) getSupportFragmentManager().findFragmentByTag("f0"); // Adjust tag based on your ViewPager
            if (tab1Fragment != null) {
                tab1Fragment.refreshCourseData();
            }
        } else {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void openNotes() {
        String courseId = getIntent().getStringExtra("courseId");
        if (courseId != null) {
            Intent intent = new Intent(this, NotesActivity.class);
            intent.putExtra("courseId", courseId); // Pass courseId to NotesActivity
            startActivity(intent);
        } else {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFlashcards() {
        // Open the Flash Cards section (e.g., using an Intent to start a new Activity)
        Intent intent = new Intent(this, FlashcardsActivity.class);
        startActivity(intent);
    }

    private void openSummarize() {
        // Open the Summarization section (e.g., using an Intent to start a new Activity)
        Intent intent = new Intent(this, SummarizerAI.class);
        startActivity(intent);
    }
}
