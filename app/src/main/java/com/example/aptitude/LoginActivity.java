package com.example.aptitude;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailLogin, passwordLogin;
    Button loginButton;
    TextView signUpRedirectText;
    ImageButton togglePasswordButton;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginButton = findViewById(R.id.loginButton);
        signUpRedirectText = findViewById(R.id.signUpRedirectText);
        togglePasswordButton = findViewById(R.id.togglePasswordButton);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If the user is already logged in, redirect to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the LoginActivity so the user cannot go back to it
        }

        // Handle Login Button Click
        loginButton.setOnClickListener(v -> {
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication Sign-In
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            // Redirect to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close LoginActivity
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Redirect to Signup Activity
        signUpRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Toggle Password Visibility
        togglePasswordButton.setOnClickListener(v -> {
            if (passwordLogin.getTransformationMethod() instanceof PasswordTransformationMethod) {
                // Show password
                passwordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordButton.setImageResource(R.drawable.ic_eye_open); // Change icon to 'open eye'
            } else {
                // Hide password
                passwordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordButton.setImageResource(R.drawable.ic_eye); // Change icon to 'closed eye'
            }

            // Move the cursor to the end of the text after transformation
            passwordLogin.setSelection(passwordLogin.length());
        });
    }
}
