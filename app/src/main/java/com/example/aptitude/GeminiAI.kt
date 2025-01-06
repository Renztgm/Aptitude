package com.example.aptitude

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class GeminiAI : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gemini)

        findViewById<View>(R.id.back_button).setOnClickListener { v: View? -> onBackPressed() } // Go back to the previous activity


        // Remove the action bar (top navigation bar)
        if (supportActionBar != null) {
            supportActionBar!!.hide() // Hide the action bar
        }


        val eTPrompt = findViewById<EditText>(R.id.eTPrompt)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val Result = findViewById<TextView>(R.id.Result)

        btnSub.setOnClickListener{
            val prompt= eTPrompt.text.toString()

            val generativeModel = GenerativeModel(

                modelName = "gemini-pro",
                apiKey = "AIzaSyC0wcSLhZiLomsKufMxO7jq52d34nKOexs"
            )
            runBlocking {
                val response = generativeModel.generateContent(prompt)
                Result.text= response.text
            }
        }
    }
}