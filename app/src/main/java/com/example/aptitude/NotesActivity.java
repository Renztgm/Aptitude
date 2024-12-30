package com.example.aptitude;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class NotesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        String courseId = getIntent().getStringExtra("courseId");

        // Fetch notes from Firestore and display them
    }
}
