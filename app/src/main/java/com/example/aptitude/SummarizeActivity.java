package com.example.aptitude;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SummarizeActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST_CODE = 1;
    private Button selectFileBtn;
    private TextView summaryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summarize);

        selectFileBtn = findViewById(R.id.selectFileBtn);
        summaryText = findViewById(R.id.summaryText);

        selectFileBtn.setOnClickListener(view -> {
            // Open file picker to select a file
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");  // Accept any type of file
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Read the file and show summary
                try {
                    String fileContent = readFileContent(fileUri);
                    String summarizedContent = summarizeText(fileContent);
                    summaryText.setText(summarizedContent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error reading the file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Read the content of the file
    private String readFileContent(Uri fileUri) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(fileUri)));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }

    // Summarize the file content (simple truncation or summarization logic)
    private String summarizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "No content available.";
        }
        // Simple summarization: truncate to the first 200 characters
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }
}
