package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.databinding.ActivityCategoryTotalBinding
import com.st10028058.prog7313_part2.ui.adapter.CategoryTotalAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

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
            DatePickerDialog(this,
                { _, year, month, day ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                    binding.etStartDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.etEndDate.setOnClickListener {
            DatePickerDialog(this,
                { _, year, month, day ->
                    val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                    binding.etEndDate.setText(date)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
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

            val dao = AppDatabase.getDatabase(this).expenseDao()
            CoroutineScope(Dispatchers.IO).launch {
                val totals = dao.getCategoryTotals(userId, startDate, endDate)
                runOnUiThread {
                    adapter.submitList(totals)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
//Code Attribution
//ChatGPT (OpenAI GPT-4)
//AI-generated code snippets to fix errors and explanations were provided using ChatGPT for Kotlin development tasks, RoomDB setup.
//Source:
//OpenAI, 2024. ChatGPT (version GPT-4) [online] Available at: https://chat.openai.com [Accessed 2 May 2025].

//Kotlin Official Documentation
//Code patterns and language features were referenced from the official Kotlin documentation, including examples related to classes, coroutines, and Android development.
//Source:
//JetBrains, 2025. Kotlin Programming Language Documentation [online] Available at: https://kotlinlang.org/docs/home.html [Accessed 2 May 2025].

//Stack Overflow
//Practical coding solutions for common errors and Firebase integration were adapted from community answers on Stack Overflow.
//Source:
//Stack Overflow, 2025. How to implement Firebase login in Kotlin? [online] Available at: https://stackoverflow.com [Accessed 2 May 2025].

//Firebase Documentation (by Google)
//The Firebase Authentication, Realtime Database, and Firestore implementation guides were followed to correctly integrate user authentication and data handling.
//Source:
//Google, 2025. Firebase Documentation – Android Authentication [online] Available at: https://firebase.google.com/docs/auth/android/start [Accessed 2 May 2025].