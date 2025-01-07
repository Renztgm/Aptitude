package com.example.aptitude

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class GeminiAI : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance() // Firestore instance
    private val auth = FirebaseAuth.getInstance() // FirebaseAuth instance
    private var userName: String? = null // Store user name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gemini)

        findViewById<View>(R.id.back_button).setOnClickListener { onBackPressed() }
        supportActionBar?.hide() // Hide the action bar

        val eTPrompt = findViewById<EditText>(R.id.eTPrompt)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val Result = findViewById<TextView>(R.id.Result)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val userInfoTextView = findViewById<TextView>(R.id.userInfoTextView) // TextView to show user info

        // Initially disable the button and hide the progress bar
        btnSub.isEnabled = false
        progressBar.visibility = View.GONE

        // Enable button only when there's text input
        eTPrompt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                btnSub.isEnabled = !s.isNullOrBlank()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Check if the user is authenticated and fetch their info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            fetchUserInfo(currentUser.uid, userInfoTextView)
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show()
        }

        // Button click handler for AI generation
        btnSub.setOnClickListener {
            val prompt = eTPrompt.text.toString()

            // If the prompt is "What is my name?", respond with the stored user name
            if (prompt.equals("What is my name?", ignoreCase = true) && userName != null) {
                Result.text = "Your name is $userName."
                return@setOnClickListener
            }

            val generativeModel = GenerativeModel(
                modelName = "gemini-pro",
                apiKey = "AIzaSyC0wcSLhZiLomsKufMxO7jq52d34nKOexs"
            )

            // Show loading indicator and disable the button
            progressBar.visibility = View.VISIBLE
            btnSub.isEnabled = false

            // Start AI request asynchronously
            runBlocking {
                try {
                    // Start the request
                    val response = generativeModel.generateContent(prompt)

                    // Update the result text
                    Result.text = response.text ?: "No response received."
                } catch (e: Exception) {
                    Result.text = "Error has occurred."
                    e.printStackTrace()
                } finally {
                    // Hide loading indicator and re-enable the button
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        btnSub.isEnabled = true
                    }
                }
            }
        }

    }

    // Function to fetch user info from Firestore and display it
    private fun fetchUserInfo(userId: String, userInfoTextView: TextView) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Extract user info from Firestore document
                    userName = document.getString("firstName")
                    val email = document.getString("email")

                    // Display the user info
                    userInfoTextView.text = "Name: $userName\nEmail: $email"
                } else {
                    Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching user info: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}
