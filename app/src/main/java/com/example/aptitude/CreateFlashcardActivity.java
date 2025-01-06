package com.example.aptitude;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CreateFlashcardActivity extends AppCompatActivity {

    private EditText etQuestion, etAnswer;
    private Button btnSave;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flashcard);

        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSave.setOnClickListener(v -> saveFlashcard());
    }

    private void saveFlashcard() {
        String question = etQuestion.getText().toString().trim();
        String answer = etAnswer.getText().toString().trim();
        String userId = auth.getCurrentUser().getUid();

        if (question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> flashcard = new HashMap<>();
        flashcard.put("question", question);
        flashcard.put("answer", answer);

        db.collection("flashcards").document(userId).collection("userFlashcards")
                .add(flashcard)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Flashcard saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving flashcard", Toast.LENGTH_SHORT).show());
    }
}
