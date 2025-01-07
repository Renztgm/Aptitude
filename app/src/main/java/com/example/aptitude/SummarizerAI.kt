package com.example.aptitude

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.aptitude.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.File

class SummarizerAI : AppCompatActivity() {
    private var summaryLength = "Medium" // Default selection
    private val scope = MainScope()

    // List of question words to detect
    private val questionWords = listOf("who", "what is", "where", "when", "why", "how")

    private val pickPdfFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val pdfText = extractTextFromPdf(it)
                val eTPrompt = findViewById<EditText>(R.id.eTPrompt)
                eTPrompt.setText(pdfText) // Set extracted text in EditText
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_summarazerai)

        val apiKey = BuildConfig.GEMINI_API_KEY
        val eTPrompt = findViewById<EditText>(R.id.eTPrompt)
        val btnSub = findViewById<Button>(R.id.btnSub)
        val result = findViewById<TextView>(R.id.Result)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupSummary)
        val btnCopy = findViewById<Button>(R.id.btnCopy)
        val btnSelectPdf = findViewById<Button>(R.id.btnSelectPdf)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Button to open the file chooser for PDF files
        btnSelectPdf.setOnClickListener {
            pickPdfFile.launch("application/pdf")  // Launch PDF picker
        }

        // Set a listener for the RadioGroup to update summaryLength based on selection
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            summaryLength = when (checkedId) {
                R.id.radioShort -> "Short"
                R.id.radioMedium -> "Medium"
                R.id.radioLong -> "Long"
                R.id.radioKeyPoints -> "KeyPoints"
                else -> "Medium"
            }
        }

        btnSub.setOnClickListener {
            val prompt = eTPrompt.text.toString().trim()

            // Detect the language of the input text
            if (questionWords.any { prompt.lowercase().startsWith(it) }) {
                result.text = "I cannot process this type of question. Please refer to Gemini AI or try to go to your Browser for more information."
            } else {
                // Check if the prompt is too short (less than a threshold)
                if (prompt.length < 40) {  // Example threshold of 40 characters for short text
                    result.text = "The provided text is too short. Please provide a longer text for summarization."
                } else {
                    // Show the progress bar while processing
                    progressBar.visibility = ProgressBar.VISIBLE

                    val generativeModel = GenerativeModel(
                        modelName = "gemini-pro",
                        apiKey = apiKey
                    )

                    val summaryPrompt = when (summaryLength) {
                        "Short" -> "Summarize this text in sentences briefly (22.5% of the length of the provided text.): $prompt"
                        "Medium" -> "Summarize this text in paragraph/s form (45% of the length of the provided text.): $prompt"
                        "Long" -> "Provide a long and detailed paragraph/s summary of this text (80% of the length of the provided text.): $prompt"
                        "KeyPoints" -> "Summarize this text in Bullet and give highlight the most important: $prompt"
                        else -> prompt
                    }

                    scope.launch {
                        try {
                            val response = generativeModel.generateContent(summaryPrompt)
                            result.text = response.text ?: "No response received."
                        } catch (e: Exception) {
                            result.text = "Error occurred: ${e.localizedMessage}"
                        } finally {
                            progressBar.visibility = ProgressBar.GONE
                        }
                    }
                }
            }
        }

        // Copy the result to clipboard when the "Copy" button is clicked
        btnCopy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Summarized Result", result.text)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(this, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to extract text from PDF using iText
    private fun extractTextFromPdf(uri: Uri): String {
        val file = File(cacheDir, "temp.pdf")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)  // Copy the PDF to a temporary file
            }
        }

        val pdfText = StringBuilder()
        try {
            val reader = PdfReader(file.absolutePath) // Load the PDF
            val numberOfPages = reader.numberOfPages
            for (i in 1..numberOfPages) {
                val pageText = PdfTextExtractor.getTextFromPage(reader, i) // Extract text from each page
                pdfText.append(pageText)
            }
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error reading PDF: ${e.localizedMessage}" // Return error message if there's an issue
        }

        return pdfText.toString() // Return extracted text
    }
}
