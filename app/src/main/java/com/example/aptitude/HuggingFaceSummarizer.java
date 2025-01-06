package com.example.aptitude;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class HuggingFaceSummarizer {

    private static final String API_URL = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";
    private static final String API_TOKEN = "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"; // Replace with your token

    public static String summarizeText(String text) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("inputs", text);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Scanner scanner = new Scanner(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Get the summarized text from the response
            JSONArray jsonResponse = new JSONArray(response.toString());
            String summary = jsonResponse.getJSONObject(0).getString("summary_text");

            // Enhance the summary by adding key points dynamically
            return enhanceSummary(summary);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error while summarizing.";
        }
    }

    // Method to dynamically add depth and key points to the summary
    private static String enhanceSummary(String summary) {
        StringBuilder enhancedSummary = new StringBuilder();

        // Overview: You can customize this logic to extract the general idea of the text.
        String overview = summary.split("\\.")[0];  // First sentence as the overview
        enhancedSummary.append("Overview:\n").append(overview).append("\n\n");

//        // Extract Important Themes: Based on the content of the summary
//        enhancedSummary.append("Important Themes:\n").append(extractThemes(summary)).append("\n\n");
//
//        // Extract Key Takeaways: Actionable insights or key messages from the summary
//        enhancedSummary.append("Key Takeaways:\n").append(extractTakeaways(summary)).append("\n\n");

        // Detailed Explanation: The full text of the summary for further context
        enhancedSummary.append("Detailed Explanation:\n").append(summary).append("\n");

        return enhancedSummary.toString();
    }

    // Method to dynamically extract important themes from the summary text
    private static String extractThemes(String summary) {
        StringBuilder themes = new StringBuilder();

        // Example theme extraction (can be improved with NLP or specific keyword analysis)
        if (summary.contains("environment") || summary.contains("climate")) {
            themes.append("Environmental issues, Climate change, Conservation.\n");
        }
//        if (summary.contains("biodiversity")) {
//            themes.append("Biodiversity, Ecosystem preservation.\n");
//        }
//        if (summary.contains("sustainability")) {
//            themes.append("Sustainability, Green technologies, Resource management.\n");
//        }

        // If no themes found, return a default message
        return themes.length() > 0 ? themes.toString() : "No specific themes identified.";
    }

    // Method to dynamically extract key takeaways from the summary text
    private static String extractTakeaways(String summary) {
        StringBuilder takeaways = new StringBuilder();

        // Example of extracting actionable insights based on the content of the summary
        if (summary.contains("climate change")) {
            takeaways.append("Urgent action is needed to combat climate change.\n");
        }
        if (summary.contains("sustainability")) {
            takeaways.append("Investing in sustainable practices is crucial for the future.\n");
        }
        if (summary.contains("biodiversity")) {
            takeaways.append("Protecting biodiversity should be a priority.\n");
        }

        // If no specific takeaways, return a default message
        return takeaways.length() > 0 ? takeaways.toString() : "No key takeaways identified.";
    }
}
