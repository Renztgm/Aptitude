package com.example.aptitude;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class FlashcardsActivity extends AppCompatActivity {

    private Button btnCreate;
    private RecyclerView recyclerView;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcardList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        findViewById(R.id.back_button).setOnClickListener(v -> onBackPressed());

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnCreate = findViewById(R.id.btnCreate);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(FlashcardsActivity.this, LoginActivity.class));
            finish();
            return;
        }

        flashcardList = new ArrayList<>();
        String userId = auth.getCurrentUser().getUid();
        adapter = new FlashcardAdapter(flashcardList, userId);  // Pass user ID to adapter
        recyclerView.setAdapter(adapter);

        loadFlashcards();

        btnCreate.setOnClickListener(v -> {
            startActivity(new Intent(FlashcardsActivity.this, CreateFlashcardActivity.class));
        });
    }

    private void loadFlashcards() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = auth.getCurrentUser().getUid();
        db.collection("flashcards").document(userId).collection("userFlashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    flashcardList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            String id = snapshot.getId();  // Get the Firestore document ID
                            String question = snapshot.getString("question");
                            String answer = snapshot.getString("answer");

                            // Create Flashcard with ID
                            Flashcard flashcard = new Flashcard(id, question, answer);
                            flashcardList.add(flashcard);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FlashcardsActivity.this, "No flashcards available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FlashcardsActivity.this, "Error loading flashcards", Toast.LENGTH_SHORT).show();
                });
    }
}
