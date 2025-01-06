package com.example.aptitude;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Tab1Fragment extends Fragment {

    private TextView selectedDateTextView;
    private FirebaseFirestore firestore;
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private ArrayList<Course> courseList;

    private String courseId; // To store the course ID if passed
    private Button add_course_button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab1, container, false);

        add_course_button = rootView.findViewById(R.id.add_course_button);
        add_course_button.setOnClickListener(v -> showAddCourseDialog());

        MaterialButton showCalendarButton = rootView.findViewById(R.id.showCalendarButton);
        showCalendarButton.setOnClickListener(v -> showDatePickerDialog());

        selectedDateTextView = rootView.findViewById(R.id.selectedDateTextView);

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
        fetchCourses();

        // Button to navigate to MapsActivity and AccountActivity
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

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    selectedDateTextView.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    private void showAddCourseDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_course, null);

        EditText courseNameEditText = dialogView.findViewById(R.id.course_name);
        EditText courseDescriptionEditText = dialogView.findViewById(R.id.course_description);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String courseName = courseNameEditText.getText().toString().trim();
            String courseDescription = courseDescriptionEditText.getText().toString().trim();
            if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
                addCourseToFirestore(courseName, courseDescription);
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addCourseToFirestore(String courseName, String courseDescription) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> courseData = new HashMap<>();
        courseData.put("name", courseName);
        courseData.put("description", courseDescription);

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .add(courseData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Course added successfully!", Toast.LENGTH_SHORT).show();
                    fetchCourses(); // Refresh course list
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add course.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        courseId = getArguments() != null ? getArguments().getString("courseId") : null;
        if (courseId != null) {
            fetchCourseById(courseId);
        } else {
            fetchCourses();
        }
    }

    public void fetchCourses() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            redirectToLogin();
            return;
        }

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String courseId = document.getId();
                            String courseName = document.getString("name");
                            String courseDescription = document.getString("description");
                            courseList.add(new Course(courseId, courseName, courseDescription));
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Failed to load courses.", Toast.LENGTH_SHORT).show();
                        checkUserAuthentication();
                    }
                });
    }

    private void checkUserAuthentication() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    public void fetchCourseById(String courseId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .document(courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String courseName = document.getString("name");
                            String courseDescription = document.getString("description");
                            Log.d("FetchCourse", "Course fetched: " + courseName + " - " + courseDescription);
                        } else {
                            Toast.makeText(getContext(), "Course not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load course.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCourseData();
    }

    public void refreshCourseData() {
        if (courseId != null) {
            fetchCourseById(courseId);
        } else {
            fetchCourses();
        }
    }
}
