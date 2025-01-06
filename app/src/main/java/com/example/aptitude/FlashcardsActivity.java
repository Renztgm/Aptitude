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

        // Initialize views
        btnCreate = findViewById(R.id.btnCreate);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);  // Make sure this is in your XML layout

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        if (auth.getCurrentUser() == null) {
            // Redirect to login activity if not logged in
            startActivity(new Intent(FlashcardsActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize the flashcard list and adapter
        flashcardList = new ArrayList<>();
        adapter = new FlashcardAdapter(flashcardList);
        recyclerView.setAdapter(adapter);

        // Load flashcards from Firestore
        loadFlashcards();

        // Create button click listener to navigate to the CreateFlashcardActivity
        btnCreate.setOnClickListener(v -> {
            startActivity(new Intent(FlashcardsActivity.this, CreateFlashcardActivity.class));
        });
    }

    private void loadFlashcards() {
        // Show the loading progress bar
        progressBar.setVisibility(View.VISIBLE);

        String userId = auth.getCurrentUser().getUid();
        db.collection("flashcards").document(userId).collection("userFlashcards")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Hide the progress bar once data is loaded
                    progressBar.setVisibility(View.GONE);

                    // Clear the existing flashcards and add the new ones
                    flashcardList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Flashcard flashcard = snapshot.toObject(Flashcard.class);
                            flashcardList.add(flashcard);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(FlashcardsActivity.this, "No flashcards available", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Hide the progress bar and show error message
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(FlashcardsActivity.this, "Error loading flashcards", Toast.LENGTH_SHORT).show();
                });
    }
}
