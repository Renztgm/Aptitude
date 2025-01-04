package com.example.aptitude;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;

public class SummarizeActivity extends AppCompatActivity {

    private EditText inputEditText;
    private TextView outputTextView;
    private Button summarizeButton;

    private static final String API_KEY = "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";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateText?key=" + API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summarize);

        inputEditText = findViewById(R.id.inputEditText);
        outputTextView = findViewById(R.id.outputTextView);
        summarizeButton = findViewById(R.id.summarizeButton);

        summarizeButton.setOnClickListener(view -> {
            String inputText = inputEditText.getText().toString();
            if (!inputText.isEmpty()) {
                summarizeText(inputText);
            } else {
                Toast.makeText(this, "Please enter text to summarize", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void summarizeText(String inputText) {
        OkHttpClient client = new OkHttpClient();

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("prompt", inputText);

        RequestBody body = RequestBody.create(
                requestBodyJson.toString(), MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(SummarizeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    runOnUiThread(() -> Toast.makeText(SummarizeActivity.this, "Empty response", Toast.LENGTH_SHORT).show());
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                    if (jsonResponse.has("candidates")) {
                        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
                        if (candidates.size() > 0) {
                            // The content is inside 'content.parts[0].text' in Gemini API responses
                            JsonObject contentObject = candidates.get(0).getAsJsonObject();
                            JsonObject content = contentObject.getAsJsonObject("content");
                            JsonArray parts = content.getAsJsonArray("parts");
                            String summary = parts.get(0).getAsJsonObject().get("text").getAsString();

                            runOnUiThread(() -> outputTextView.setText(summary));
                        } else {
                            runOnUiThread(() -> Toast.makeText(SummarizeActivity.this, "No summary received.", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(SummarizeActivity.this, "Invalid response format.", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(SummarizeActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
