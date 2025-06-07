package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityEditExpenseBinding
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditExpenseBinding
    private var documentId: String = ""
    private var currentPhotoUrl: String? = null
    private var selectedPhotoUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedPhotoUri = it
                binding.imgPhoto.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        documentId = intent.getStringExtra("expense_doc_id") ?: ""
        if (documentId.isEmpty()) {
            Toast.makeText(this, "Invalid expense reference", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpenseData(documentId)

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    binding.etDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.imgPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            updateExpense(userId)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadExpenseData(docId: String) {
        val db = Firebase.firestore
        db.collection("expenses").document(docId).get()
            .addOnSuccessListener { doc ->
                val expense = doc.toObject(Expense::class.java)
                if (expense != null) {
                    binding.etDate.setText(expense.date)
                    binding.etDescription.setText(expense.description)
                    binding.etCategory.setText(expense.category)
                    binding.etAmount.setText(expense.amount.toString())
                    currentPhotoUrl = expense.photoPath
                    expense.photoPath?.let {
                        binding.imgPhoto.setImageURI(Uri.parse(it))
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load expense", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateExpense(userId: String) {
        val date = binding.etDate.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amount = binding.etAmount.text.toString().trim().toDoubleOrNull()

        if (date.isEmpty() || desc.isEmpty() || category.isEmpty() || amount == null) {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPhotoUri != null) {
            uploadNewPhoto { url ->
                saveUpdatedExpense(userId, date, desc, category, amount, url)
            }
        } else {
            saveUpdatedExpense(userId, date, desc, category, amount, currentPhotoUrl)
        }
    }

    private fun uploadNewPhoto(onUploaded: (String?) -> Unit) {
        val fileRef = FirebaseStorage.getInstance().reference
            .child("receipts/${UUID.randomUUID()}.jpg")

        selectedPhotoUri?.let { uri ->
            fileRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    fileRef.downloadUrl
                }
                .addOnSuccessListener { onUploaded(it.toString()) }
                .addOnFailureListener {
                    Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show()
                    onUploaded(null)
                }
        }
    }

    private fun saveUpdatedExpense(
        userId: String,
        date: String,
        desc: String,
        category: String,
        amount: Double,
        photoUrl: String?
    ) {
        val db = Firebase.firestore
        val expense = Expense(
            id = documentId,
            userId = userId,
            date = date,
            description = desc,
            category = category,
            amount = amount,
            photoPath = photoUrl
        )

        db.collection("expenses").document(documentId).set(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show()
            }
    }
}

//Code Attribution

//# Code and support generated with the help of OpenAI's ChatGPT.
//ChatGPT (OpenAI GPT-4)

//Kotlin Official Documentation
//Code patterns and language features were referenced from the official Kotlin documentation, including examples related to classes, coroutines, and Android development.
//Source:
//JetBrains, 2025. Kotlin Programming Language Documentation [online] Available at: https://kotlinlang.org/docs/home.html [Accessed 2 May 2025].

// W3schools / /https://www.w3schools.com/kotlin/index.php

//Stack Overflow
//Practical coding solutions for common errors and Firebase integration were adapted from community answers on Stack Overflow.
//Source:
//Stack Overflow, 2025. How to implement Firebase login in Kotlin? [online] Available at: https://stackoverflow.com [Accessed 2 May 2025].

//Firebase Documentation (by Google)
//The Firebase Authentication, Realtime Database, and Firestore implementation guides were followed to correctly integrate user authentication and data handling.
//Source:
//Google, 2025. Firebase Documentation – Android Authentication [online] Available at: https://firebase.google.com/docs/auth/android/start [Accessed 2 May 2025].