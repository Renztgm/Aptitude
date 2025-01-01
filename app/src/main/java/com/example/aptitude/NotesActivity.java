package com.example.aptitude;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotesActivity extends AppCompatActivity {

    private EditText notesEditText;
    private MaterialButton saveNotesButton;
    private FirebaseFirestore firestore;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the courseId passed from CourseActivity
        courseId = getIntent().getStringExtra("courseId");

        // Initialize UI components
        notesEditText = findViewById(R.id.notes_edit_text);
        saveNotesButton = findViewById(R.id.save_notes_button);

        // Check if courseId is not null
        if (courseId == null) {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve and display existing notes from Firestore (if any)
        fetchNotes();

        // Set up click listener for save button
        saveNotesButton.setOnClickListener(v -> saveNotes());
    }

    private void fetchNotes() {
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .collection("notes") // Assuming you have a subcollection called "notes"
                .document("noteId") // Assuming you're using a specific document ID for the notes
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get existing note and set it in the EditText
                        String existingNotes = documentSnapshot.getString("noteText");
                        if (existingNotes != null) {
                            notesEditText.setText(existingNotes);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load notes", Toast.LENGTH_SHORT).show();
                    Log.e("NotesActivity", "Error fetching notes: " + e.getMessage());
                });
    }

    private void saveNotes() {
        String notesText = notesEditText.getText().toString().trim();

        if (notesText.isEmpty()) {
            Toast.makeText(this, "Notes cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the notes to Firestore
        firestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("courses")
                .document(courseId)
                .collection("notes")
                .document("noteId") // You can dynamically generate or choose a document ID
                .set(new Note(notesText)) // Save the note text
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotesActivity.this, "Notes saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NotesActivity.this, "Failed to save notes", Toast.LENGTH_SHORT).show();
                    Log.e("NotesActivity", "Error saving notes: " + e.getMessage());
                });
    }

    // Define a Note class to represent the note object
    public static class Note {
        private String noteText;

        public Note(String noteText) {
            this.noteText = noteText;
        }

        public String getNoteText() {
            return noteText;
        }

        public void setNoteText(String noteText) {
            this.noteText = noteText;
        }
    }
}
