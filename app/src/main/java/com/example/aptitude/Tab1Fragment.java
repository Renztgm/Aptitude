package com.example.aptitude;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Tab1Fragment extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private ArrayList<Course> courseList;

    private String courseId; // To store the course ID if passed

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize RecyclerView with StaggeredGridLayoutManager
        coursesRecyclerView = rootView.findViewById(R.id.courses_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        coursesRecyclerView.setLayoutManager(layoutManager);

        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(requireContext(), courseList);
        coursesRecyclerView.setAdapter(courseAdapter);

        // Fetch courses when fragment is created
        fetchCourses(); // This will fetch all courses if no specific courseId is provided

        // Handle button actions
        rootView.findViewById(R.id.search_button).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });

        rootView.findViewById(R.id.account).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore and fetch courseId if available
        firestore = FirebaseFirestore.getInstance();
        courseId = getArguments() != null ? getArguments().getString("courseId") : null;

        if (courseId != null) {
            // Fetch a specific course by courseId if it's passed
            fetchCourseById(courseId);
        } else {
            // Fetch all courses if courseId is not available
            fetchCourses();
        }
    }

    // Fetches the course list from Firestore (fetching all courses)
    public void fetchCourses() {
        Log.d("FetchCourses", "fetchCourses called.");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .get() // Get all courses
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        courseList.clear(); // Clear existing data
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String courseId = document.getId();
                                String courseName = document.getString("name");
                                String courseDescription = document.getString("description");

                                // Add course to list
                                courseList.add(new Course(courseId, courseName, courseDescription));
                            }
                            courseAdapter.notifyDataSetChanged(); // Notify adapter to update the RecyclerView
                            Log.d("FetchCourses", "Courses fetched and adapter updated.");
                        } else {
                            Log.d("FetchCourses", "No courses found.");
                            Toast.makeText(getContext(), "No courses available.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FetchCourses", "Error fetching courses: ", task.getException());
                        Toast.makeText(getContext(), "Failed to load courses.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Fetch a specific course by courseId from Firestore
    public void fetchCourseById(String courseId) {
        Log.d("FetchCourse", "fetchCourseById called for courseId: " + courseId);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(courseId) // Fetch a single course document by courseId
                .get() // Using get() to fetch a single document
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String courseName = document.getString("name");
                            String courseDescription = document.getString("description");

                            // Handle the fetched course details (e.g., populate UI or process data)
                            Log.d("FetchCourse", "Course fetched: " + courseName + " - " + courseDescription);
                        } else {
                            Log.d("FetchCourse", "Course not found.");
                            Toast.makeText(getContext(), "Course not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FetchCourse", "Error fetching course: ", task.getException());
                        Toast.makeText(getContext(), "Failed to load course.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to refresh course data (called from another activity/fragment if needed)
    public void refreshCourseData() {
        if (courseId != null) {
            fetchCourseById(courseId); // Fetch specific course if courseId is available
        } else {
            fetchCourses(); // Fetch all courses if courseId is not available
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Refresh the course data every time the fragment is visible again
        refreshCourseData();
    }
}
