package com.example.aptitude;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCourseDialog extends Dialog {

    private final String courseId;
    private final FirebaseFirestore firestore;
    private final Runnable onCourseUpdated; // Callback for notifying course updates

    public EditCourseDialog(
            @NonNull Context context,
            @NonNull String courseId,
            @NonNull FirebaseFirestore firestore,
            @NonNull Runnable onCourseUpdated
    )
    {
        super(context);
        this.courseId = courseId;
        this.firestore = firestore;
        this.onCourseUpdated = onCourseUpdated;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_course);

        // Initialize the dialog elements
        EditText editCourseName = findViewById(R.id.edit_course_name);
        EditText editCourseDescription = findViewById(R.id.edit_course_description);
        Button saveButton = findViewById(R.id.save_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Fetch existing course details and populate the fields
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editCourseName.setText(documentSnapshot.getString("name"));
                        editCourseDescription.setText(documentSnapshot.getString("description"));
                    } else {
                        Toast.makeText(getContext(), "Course not found", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditCourseDialog", "Failed to fetch course details: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to fetch course details", Toast.LENGTH_SHORT).show();
                    dismiss();
                });

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            String newName = editCourseName.getText().toString().trim();
            String newDescription = editCourseDescription.getText().toString().trim();

            if (newName.isEmpty() || newDescription.isEmpty()) {
                Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("courses")
                    .document(courseId)
                    .update("name", newName, "description", newDescription)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Course updated successfully", Toast.LENGTH_SHORT).show();
                        if (onCourseUpdated != null) {
                            onCourseUpdated.run(); // Notify the activity
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EditCourseDialog", "Failed to update course: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to update course", Toast.LENGTH_SHORT).show();
                    });
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> dismiss());
    }
}
