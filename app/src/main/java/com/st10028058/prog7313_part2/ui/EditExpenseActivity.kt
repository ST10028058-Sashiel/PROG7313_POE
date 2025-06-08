package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityEditExpenseBinding
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditExpenseBinding
    private lateinit var documentId: String
    private var currentPhotoUrl: String? = null
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imgPhoto.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        documentId = intent.getStringExtra("expense_doc_id") ?: ""
        if (documentId.isEmpty()) {
            Toast.makeText(this, "No valid expense ID provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpenseData()

        binding.etDate.setOnClickListener {
            DatePickerDialog(this,
                { _, y, m, d ->
                    binding.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.imgPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            updateExpense(user.uid)
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadExpenseData() {
        Firebase.firestore.collection("expenses").document(documentId).get()
            .addOnSuccessListener { doc ->
                doc.toObject(Expense::class.java)?.let { exp ->
                    binding.etDate.setText(exp.date)
                    binding.etDescription.setText(exp.description)
                    binding.etCategory.setText(exp.category)
                    binding.etAmount.setText(exp.amount.toString())
                    currentPhotoUrl = exp.photoPath
                    exp.photoPath?.let {
                        binding.imgPhoto.setImageURI(Uri.parse(it))
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading expense", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateExpense(userId: String) {
        val date = binding.etDate.text.toString().trim()
        val desc = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amount = binding.etAmount.text.toString().toDoubleOrNull()

        if (date.isEmpty() || desc.isEmpty() || category.isEmpty() || amount == null) {
            return Toast.makeText(this, "Fill in all fields correctly", Toast.LENGTH_SHORT).show()
        }

        if (selectedImageUri != null) {
            uploadPhotoAndSave(userId, date, desc, category, amount)
        } else {
            saveExpense(userId, date, desc, category, amount, currentPhotoUrl)
        }
    }

    private fun uploadPhotoAndSave(userId: String, date: String, desc: String, category: String, amount: Double) {
        val storageRef = FirebaseStorage.getInstance()
            .reference.child("receipts/${UUID.randomUUID()}.jpg")
        selectedImageUri?.let { uri ->
            storageRef.putFile(uri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) throw task.exception!!
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { downloadUri ->
                    saveExpense(userId, date, desc, category, amount, downloadUri.toString())
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveExpense(userId: String, date: String, desc: String, category: String, amount: Double, photoUrl: String?) {
        val expense = Expense(
            id = documentId,
            userId = userId,
            date = date,
            description = desc,
            category = category,
            amount = amount,
            photoPath = photoUrl
        )
        Firebase.firestore.collection("expenses").document(documentId).set(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
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