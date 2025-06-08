package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityFilterExpensesBinding
import com.st10028058.prog7313_part2.ui.adapter.ExpenseAdapter
import java.util.*

class FilterExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterExpensesBinding
    private lateinit var adapter: ExpenseAdapter
    private lateinit var startDate: String
    private lateinit var endDate: String
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (userId.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        adapter = ExpenseAdapter(
            onItemClick = { expense ->
                val intent = Intent(this, ExpenseDetailActivity::class.java)
                intent.putExtra("expense_doc_id", expense.id)
                startActivity(intent)
            },
            onItemDelete = { expense ->
                deleteExpense(expense.id)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.etStartDate.setOnClickListener {
            showDatePicker { date -> binding.etStartDate.setText(date) }
        }

        binding.etEndDate.setOnClickListener {
            showDatePicker { date -> binding.etEndDate.setText(date) }
        }

        binding.btnFilter.setOnClickListener {
            startDate = binding.etStartDate.text.toString()
            endDate = binding.etEndDate.text.toString()
            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Select both dates", Toast.LENGTH_SHORT).show()
            } else {
                reloadExpenses()
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val date = String.format("%04d-%02d-%02d", y, m + 1, d)
                onDateSet(date)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun reloadExpenses() {
        Firebase.firestore.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snap ->
                val filtered = snap.toObjects(Expense::class.java)
                    .filter { it.date in startDate..endDate }
                adapter.submitList(filtered)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteExpense(docId: String) {
        Firebase.firestore.collection("expenses")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                reloadExpenses()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
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