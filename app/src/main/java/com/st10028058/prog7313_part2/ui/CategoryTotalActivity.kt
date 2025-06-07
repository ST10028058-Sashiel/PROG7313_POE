package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.data.CategoryTotal
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityCategoryTotalBinding
import com.st10028058.prog7313_part2.ui.adapter.CategoryTotalAdapter
import java.util.*
import kotlin.collections.HashMap

class CategoryTotalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryTotalBinding
    private val adapter = CategoryTotalAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryTotalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val calendar = Calendar.getInstance()

        binding.etStartDate.setOnClickListener {
            showDatePicker { date -> binding.etStartDate.setText(date) }
        }

        binding.etEndDate.setOnClickListener {
            showDatePicker { date -> binding.etEndDate.setText(date) }
        }

        binding.btnFilter.setOnClickListener {
            val startDate = binding.etStartDate.text.toString()
            val endDate = binding.etEndDate.text.toString()

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please enter both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId.isNullOrEmpty()) {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchCategoryTotalsFromFirestore(userId, startDate, endDate)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                onDateSet(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun fetchCategoryTotalsFromFirestore(userId: String, startDate: String, endDate: String) {
        val db = Firebase.firestore

        db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val expenses = result.toObjects(Expense::class.java)
                    .filter { it.date >= startDate && it.date <= endDate }

                val categoryMap = HashMap<String, Double>()
                for (expense in expenses) {
                    val currentTotal = categoryMap[expense.category] ?: 0.0
                    categoryMap[expense.category] = currentTotal + expense.amount
                }

                val categoryTotals = categoryMap.map { CategoryTotal(it.key, it.value) }
                adapter.submitList(categoryTotals)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show()
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