package com.example.aptitude;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab1Fragment extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private ArrayList<Course> courseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        // Fetch courses for the user
        fetchCourses();

        // Handle button actions
        rootView.findViewById(R.id.search_button).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapsActivity.class);
            startActivity(intent);
        });

        rootView.findViewById(R.id.account).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        rootView.findViewById(R.id.add_course_button).setOnClickListener(v -> showAddCourseDialog());

        return rootView;
    }

    private void showAddCourseDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_course, null);

        EditText courseNameEditText = dialogView.findViewById(R.id.course_name);
        EditText courseDescriptionEditText = dialogView.findViewById(R.id.course_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String courseName = courseNameEditText.getText().toString().trim();
            String courseDescription = courseDescriptionEditText.getText().toString().trim();

            if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
                addCourseToFirestore(courseName, courseDescription);

                // Notify MainActivity to refresh Tab1
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).refreshTab1();
                }
            } else {
                Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }



    private void addCourseToFirestore(String courseName, String courseDescription) {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a map for course data
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("name", courseName);
        courseData.put("description", courseDescription);

        // Add course to Firestore under the user's UID
        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .add(courseData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Course added successfully!", Toast.LENGTH_SHORT).show();
                    fetchCourses(); // Refresh the list of courses
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add course. Try again.", Toast.LENGTH_SHORT).show();
                });
    }



    public void fetchCourses() {
        Log.d("FetchCourses", "fetchCourses called.");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        courseList.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            courseList.add(new Course(name, description));
                        }
                        courseAdapter.notifyDataSetChanged();
                        Log.d("FetchCourses", "Courses fetched and adapter updated.");
                    } else {
                        Toast.makeText(getContext(), "Failed to load courses.", Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
