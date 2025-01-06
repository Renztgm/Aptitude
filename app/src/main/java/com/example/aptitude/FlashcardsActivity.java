package com.example.aptitude;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FlashcardsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed()); // Go back to the previous activity

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        String courseId = getIntent().getStringExtra("courseId");

        // Fetch flashcards from Firestore and display them
    }
}
