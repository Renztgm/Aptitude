package com.example.aptitude;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountActivity extends AppCompatActivity {

    // Declare the FirebaseAuth instance, Firestore instance, and TextViews for name, email, and birthdate
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private TextView usernameTextView, emailTextView, birthdateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Remove the status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars()); // Hides the status bar
            }
        } else {
            // For devices below Android 11, use this method
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // Remove the action bar (top navigation bar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide(); // Hide the action bar
        }

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize the TextViews
        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        birthdateTextView = findViewById(R.id.birthdate);

        // Get the current user from Firebase Authentication
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // Display the user's email in the TextView
            String email = user.getEmail();
            emailTextView.setText(email);

            // Get user data from Firestore
            String userId = user.getUid();
            mFirestore.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Get the user's first name, last name, and birthdate from Firestore
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String birthdate = document.getString("birthdate");

                                // Set the user's name and birthdate in the TextViews
                                if (firstName != null && lastName != null) {
                                    usernameTextView.setText(firstName + " " + lastName);
                                }
                                if (birthdate != null) {
                                    birthdateTextView.setText("Birthdate: " + birthdate);
                                }
                            }
                        } else {
                            // If there's an error getting user data, show a toast
                            Toast.makeText(AccountActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // If no user is logged in, show a default message or handle accordingly
            emailTextView.setText("No user logged in");
            usernameTextView.setText("No name available");
            birthdateTextView.setText("No birthdate available");
        }

        // Button to handle logout
        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            // Perform logout with FirebaseAuth
            mAuth.signOut();

            // Display a toast message
            Toast.makeText(AccountActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();

            // Navigate to LoginActivity (or your login screen)
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close this activity so the user can't go back to the account page after logging out
        });

        // Back button to navigate back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); // Navigates to the previous screen
    }
}
