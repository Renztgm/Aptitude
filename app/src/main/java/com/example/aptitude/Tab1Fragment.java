package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Tab1Fragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        // Find the button in the layout
        Button openActivityButton = rootView.findViewById(R.id.search_button);

        // Set up a click listener for the button
        openActivityButton.setOnClickListener(v -> {
            // Create an intent to open the MapsActivity
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });

        // Account button to open AccountActivity
        Button accountButton = rootView.findViewById(R.id.account); // Button ID from XML
        accountButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        // Handle click for Course 1
        Button courseButton1 = rootView.findViewById(R.id.course_button_1);
        courseButton1.setOnClickListener(v -> {
            openCourseActivity("Course 1");
        });

        // Handle click for Course 2
        Button courseButton2 = rootView.findViewById(R.id.course_button_2);
        courseButton2.setOnClickListener(v -> {
            openCourseActivity("Course 2");
        });

        return rootView;
    }

    private void openCourseActivity(String courseId) {
        // Create an intent to open the CourseActivity with the course ID
        Intent intent = new Intent(getActivity(), CourseActivity.class);
        intent.putExtra("course_id", courseId); // Pass the course ID to the new activity
        startActivity(intent);
    }
}
