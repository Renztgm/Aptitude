package com.example.aptitude;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText emailSignup, passwordSignup, firstNameEditText, lastNameEditText;
    private Button signupButton;
    private TextView loginRedirectText, birthdateEditText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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

        // Initialize FirebaseAuth and Firestore
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        emailSignup = findViewById(R.id.emailSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        firstNameEditText = findViewById(R.id.firstNameSignup);
        lastNameEditText = findViewById(R.id.lastNameSignup);
        birthdateEditText = findViewById(R.id.birthdateSignup);  // DateText view
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        // Setup DatePicker for Birthdate
        birthdateEditText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Set selected date to the EditText
                        birthdateEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    }, year, month, day);
            datePickerDialog.show();
        });

        signupButton.setOnClickListener(v -> {
            String email = emailSignup.getText().toString().trim();
            String password = passwordSignup.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String birthdate = birthdateEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignupActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Capitalize the first and last names
            firstName = capitalizeName(firstName);
            lastName = capitalizeName(lastName);

            // Register with Firebase Authentication
            String finalFirstName = firstName;
            String finalLastName = lastName;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();

                            // Create a Map to store user data
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", finalFirstName);
                            user.put("lastName", finalLastName);
                            user.put("email", email);
                            user.put("birthdate", birthdate);

                            // Save user data in Firestore
                            mFirestore.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        // Success - Data saved to Firestore
                                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failure - Error saving data to Firestore
                                        Toast.makeText(SignupActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            // Authentication failed
                            Toast.makeText(SignupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        loginRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Helper method to capitalize first letter of each name
    private String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        // Capitalize the first letter and make the rest lowercase
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
}
