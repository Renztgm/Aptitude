package com.example.aptitude;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CourseActivity extends AppCompatActivity {

    private TextView courseTitleTextView;
    private TextView courseDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Remove the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars()); // Hides the status bar
            }
        } else {
            // For devices below Android 11, use this method
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        ImageButton backButton = findViewById(R.id.back_button);

        // Set up the click listener for the back button
        backButton.setOnClickListener(v -> {
            // Call onBackPressed to navigate back
            onBackPressed();
        });

        // Get the course ID passed from Tab1Fragment
        String courseId = getIntent().getStringExtra("course_id");

        // Set up the TextViews
        courseTitleTextView = findViewById(R.id.course_title);
        courseDescriptionTextView = findViewById(R.id.course_description);

        // Set the course title and description based on the course ID
        // Here you can load course details based on the courseId, for simplicity, we’ll use static text
        if ("Course 1".equals(courseId)) {
            courseTitleTextView.setText("Course 1");
            courseDescriptionTextView.setText("This is the description for Course 1.");
        } else if ("Course 2".equals(courseId)) {
            courseTitleTextView.setText("Course 2");
            courseDescriptionTextView.setText("This is the description for Course 2.");
        }
    }
}
