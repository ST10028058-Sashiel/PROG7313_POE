package com.st10028058.prog7313_part2.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.databinding.ActivityExpenseDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("expense_id", -1)
        val dao = AppDatabase.getDatabase(this).expenseDao()

        CoroutineScope(Dispatchers.IO).launch {
            val expense = dao.getExpenseById(id)
            if (expense == null) {
                runOnUiThread {
                    Toast.makeText(this@ExpenseDetailActivity, "Expense not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
                return@launch
            }

            runOnUiThread {
                binding.tvDate.text = expense.date
                binding.tvDescription.text = expense.description
                binding.tvCategory.text = expense.category
                binding.tvAmount.text = String.format(Locale.getDefault(), "R %.2f", expense.amount)

                if (!expense.photoPath.isNullOrEmpty()) {
                    binding.imgPhoto.setImageURI(Uri.parse(expense.photoPath))
                    binding.tvNoImage.visibility = android.view.View.GONE
                } else {
                    binding.imgPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
                    binding.tvNoImage.visibility = android.view.View.VISIBLE
                }

                binding.btnEdit.setOnClickListener {
                    val intent = Intent(this@ExpenseDetailActivity, EditExpenseActivity::class.java)
                    intent.putExtra("edit_mode", true)
                    intent.putExtra("expense_id", id)
                    startActivity(intent)
                    finish()
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