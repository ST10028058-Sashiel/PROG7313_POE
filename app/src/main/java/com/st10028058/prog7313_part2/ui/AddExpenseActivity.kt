package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityAddExpenseBinding
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etDate.setOnClickListener { showDatePicker() }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedPhotoUri = it
                binding.imgPhoto.setImageURI(it)
            }
        }

        binding.btnSelectPhoto.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.etDate.setText(dateStr)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveExpense() {
        val date = binding.etDate.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()

        if (date.isEmpty() || description.isEmpty() || category.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid number for amount", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPhotoUri != null) {
            uploadPhotoToFirebase(selectedPhotoUri!!) { photoUrl ->
                createExpenseInFirestore(userId, date, description, category, amount, photoUrl)
            }
        } else {
            createExpenseInFirestore(userId, date, description, category, amount, null)
        }
    }

    private fun uploadPhotoToFirebase(uri: Uri, onUploaded: (String?) -> Unit) {
        val fileName = "receipts/${UUID.randomUUID()}.jpg"
        val fileRef = FirebaseStorage.getInstance().reference.child(fileName)

        fileRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                fileRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                onUploaded(downloadUrl.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Photo upload failed", Toast.LENGTH_SHORT).show()
                onUploaded(null)
            }
    }

    private fun createExpenseInFirestore(
        userId: String,
        date: String,
        description: String,
        category: String,
        amount: Double,
        photoUrl: String?
    ) {
        val expenseId = Firebase.firestore.collection("expenses").document().id
        val expense = Expense(
            id = expenseId,
            userId = userId,
            date = date,
            description = description,
            category = category,
            amount = amount,
            photoPath = photoUrl
        )

        Firebase.firestore.collection("expenses").document(expenseId)
            .set(expense)
            .addOnSuccessListener {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
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