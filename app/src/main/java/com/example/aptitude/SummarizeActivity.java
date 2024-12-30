package com.example.aptitude;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SummarizeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summarize);

        String courseId = getIntent().getStringExtra("courseId");

        // Fetch summarization from Firestore and display it
    }
}
