package com.example.aptitude;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import android.widget.EditText;
import android.app.AlertDialog;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Initialize ViewPager and BottomNavigationView
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView); // Correct reference
        FloatingActionButton fab = findViewById(R.id.fab);

        // Set up ViewPager2 with an adapter
        viewPager.setAdapter(new FragmentAdapter(this));
        viewPager.setUserInputEnabled(false); // Disabling swiping

        // Link BottomNavigationView menu with ViewPager2
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.tab1) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (item.getItemId() == R.id.tab2) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (item.getItemId() == R.id.tab3) {
                viewPager.setCurrentItem(2, true);
                return true;
            } else if (item.getItemId() == R.id.tab4) {
                viewPager.setCurrentItem(3, true);
                return true;
            } else {
                return false;
            }
        });

        // Link ViewPager2 swipe events to BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Update BottomNavigationView's selected item based on the current ViewPager position
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.tab1);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.tab2);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.tab3);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.tab4);
                        break;
                    default:
                        bottomNavigationView.setSelectedItemId(R.id.tab1);
                        break;
                }
            }
        });

        // Set up FAB to open the add course dialog
        fab.setOnClickListener(view -> {
            // Show the dialog for adding a course
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_course, null);

            EditText courseNameEditText = dialogView.findViewById(R.id.course_name);
            EditText courseDescriptionEditText = dialogView.findViewById(R.id.course_description);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String courseName = courseNameEditText.getText().toString().trim();
                String courseDescription = courseDescriptionEditText.getText().toString().trim();

                if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
                    addCourseToFirestore(courseName, courseDescription);
                } else {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

    }

    // This method will be used to show the add course dialog when the FAB is clicked
    private void showAddCourseDialog() {
        // Use getLayoutInflater() instead of requireContext() for inflating the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_course, null);

        // Get the EditText fields
        EditText courseNameEditText = dialogView.findViewById(R.id.course_name);
        EditText courseDescriptionEditText = dialogView.findViewById(R.id.course_description);

        // Set up the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Handle the "Save" button click
        builder.setPositiveButton("Save", (dialog, which) -> {
            String courseName = courseNameEditText.getText().toString();
            String courseDescription = courseDescriptionEditText.getText().toString();

            // Save the course to Firebase
            addCourseToFirestore(courseName, courseDescription);
        });

        // Handle the "Cancel" button click
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

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
                .document(userId) // Document ID is the user's UID
                .collection("courses") // Subcollection for courses
                .add(courseData)
                .addOnSuccessListener(documentReference -> {
                    String generatedCourseId = documentReference.getId(); // Auto-generated ID for the course
                    Toast.makeText(MainActivity.this, "Course added successfully! Course ID: " + generatedCourseId, Toast.LENGTH_SHORT).show();
                    // Optionally: Store or display the generated course ID for future use
                    fetchCourses();
                    refreshTab1();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to add course. Try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchCourses() {
        // Get the current user's UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        // Process the courses
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to load courses.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class FragmentAdapter extends FragmentStateAdapter {

        public FragmentAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Tab1Fragment();
                case 1:
                    return new Tab2Fragment();
                case 2:
                    return new Tab3Fragment();
                case 3:
                    return new Tab4Fragment();
                default:
                    return new Tab1Fragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4; // Corrected to 4 for the number of tabs
        }
    }
    public void refreshTab1() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof Tab1Fragment) {
                ((Tab1Fragment) fragment).fetchCourses(); // Trigger list refresh
                Log.d("RefreshTab1", "Tab1Fragment refreshed successfully.");
                return;
            }
        }
        Log.d("RefreshTab1", "Tab1Fragment not found.");
    }


}
