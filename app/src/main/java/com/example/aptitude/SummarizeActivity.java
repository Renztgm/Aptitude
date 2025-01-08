package com.example.aptitude;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class SummarizeActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView resultText;
    private Button summarizeButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summarize);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        inputText = findViewById(R.id.inputText);
        resultText = findViewById(R.id.resultText);
        summarizeButton = findViewById(R.id.summarizeButton);
        progressBar = findViewById(R.id.progressBar);

        // Hide the progress bar initially
        progressBar.setVisibility(ProgressBar.GONE);

        summarizeButton.setOnClickListener(v -> summarizeInputText());

        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed()); // Go back to the previous activity

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

    }

    private void summarizeInputText() {
        String text = inputText.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter text to summarize.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the progress bar while summarizing
        progressBar.setVisibility(ProgressBar.VISIBLE);

        // Run the API call on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            String summary = HuggingFaceSummarizer.summarizeText(text);

            // Hide the progress bar and show the summary on the UI thread
            runOnUiThread(() -> {
                progressBar.setVisibility(ProgressBar.GONE);
                resultText.setText(summary);
            });
        });
    }
}
