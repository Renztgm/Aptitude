package com.example.aptitude;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class AccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private TextView usernameTextView, emailTextView, birthdateTextView;
    private ImageView profileImageView;

    private static final int PICK_IMAGE_REQUEST = 1;  // For image selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        usernameTextView = findViewById(R.id.username);
        emailTextView = findViewById(R.id.email);
        birthdateTextView = findViewById(R.id.birthdate);
        profileImageView = findViewById(R.id.profile_picture);

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            emailTextView.setText(email);

            String userId = user.getUid();
            mFirestore.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String birthdate = document.getString("birthdate");

                                if (firstName != null && lastName != null) {
                                    usernameTextView.setText(firstName + " " + lastName);
                                }
                                if (birthdate != null) {
                                    birthdateTextView.setText("Birthdate: " + birthdate);
                                }

                                String profilePictureUrl = document.getString("profilePictureUrl");
                                if (profilePictureUrl != null) {
                                    Glide.with(this).load(profilePictureUrl).into(profileImageView);
                                }
                            }
                        } else {
                            Toast.makeText(AccountActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            emailTextView.setText("No user logged in");
            usernameTextView.setText("No name available");
            birthdateTextView.setText("No birthdate available");
        }

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(AccountActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AccountActivity.this, LoginActivity.class));
            finish();
        });

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        Button editProfilePictureButton = findViewById(R.id.edit_profile_picture_button);
        editProfilePictureButton.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);

                uploadImageToFirebase(imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            StorageReference storageReference = mStorage.getReference().child("profile_pictures/" + userId + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String profilePictureUrl = uri.toString();
                        mFirestore.collection("users").document(userId)
                                .update("profilePictureUrl", profilePictureUrl)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                                        Glide.with(this).load(profilePictureUrl).into(profileImageView);
                                    } else {
                                        Toast.makeText(AccountActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }));
        }
    }
}
