package com.st10028058.prog7313_part2.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityExpenseDetailBinding
import java.util.*

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        documentId = intent.getStringExtra("expense_doc_id") ?: ""
        if (documentId.isEmpty()) {
            Toast.makeText(this, "Invalid expense ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpense(documentId)

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditExpenseActivity::class.java)
            intent.putExtra("expense_doc_id", documentId)
            startActivity(intent)
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadExpense(docId: String) {
        val db = Firebase.firestore
        db.collection("expenses").document(docId).get()
            .addOnSuccessListener { document ->
                val expense = document.toObject(Expense::class.java)
                if (expense != null) {
                    binding.tvDate.text = expense.date
                    binding.tvDescription.text = expense.description
                    binding.tvCategory.text = expense.category
                    binding.tvAmount.text = String.format(Locale.getDefault(), "R %.2f", expense.amount)

                    if (!expense.photoPath.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(expense.photoPath)
                            .into(binding.imgPhoto)
                        binding.tvNoImage.visibility = View.GONE
                    } else {
                        binding.imgPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
                        binding.tvNoImage.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load expense", Toast.LENGTH_SHORT).show()
                finish()
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