package com.example.aptitude;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.EdgeTreatment;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import android.widget.EditText;
import android.app.AlertDialog;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request storage permissions
        requestStoragePermission();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tasksCollection = db.collection("tasks");


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
//            } else if (item.getItemId() == R.id.tab3) {
//                viewPager.setCurrentItem(2, true);
//                return true;
//            } else if (item.getItemId() == R.id.tab4) {
//                viewPager.setCurrentItem(3, true);
//                return true;
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
//                    case 2:
//                        bottomNavigationView.setSelectedItemId(R.id.tab3);
//                        break;
//                    case 3:
//                        bottomNavigationView.setSelectedItemId(R.id.tab4);
//                        break;
                    default:
                        bottomNavigationView.setSelectedItemId(R.id.tab1);
                        break;
                }
            }
        });

        // Set up FAB to open the add course dialog
//        fab.setOnClickListener(view -> {
//            // Show the dialog for adding a course
//            LayoutInflater inflater = getLayoutInflater();
//            View dialogView = inflater.inflate(R.layout.dialog_add_course, null);
//
//            EditText courseNameEditText = dialogView.findViewById(R.id.course_name);
//            EditText courseDescriptionEditText = dialogView.findViewById(R.id.course_description);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setView(dialogView);
//
//            builder.setPositiveButton("Save", (dialog, which) -> {
//                String courseName = courseNameEditText.getText().toString().trim();
//                String courseDescription = courseDescriptionEditText.getText().toString().trim();
//
//                if (!courseName.isEmpty() && !courseDescription.isEmpty()) {
//                    addCourseToFirestore(courseName, courseDescription);
//                } else {
//                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//            builder.create().show();
//        });

        fab.setOnClickListener(view -> {
            // Create an Intent to navigate to the Gemini AI activity
            Intent intent = new Intent(MainActivity.this, GeminiAI.class); // Replace with your Gemini AI activity class name
            startActivity(intent);
        });
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, request specific media permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            }, STORAGE_PERMISSION_CODE);
        } else {
            // For Android 12 and below, request broader storage permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }

    // Handling the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (Environment.isExternalStorageManager()) {
                Toast.makeText(this, "Storage permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permissions denied!", Toast.LENGTH_SHORT).show();
            }
        }
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
}
