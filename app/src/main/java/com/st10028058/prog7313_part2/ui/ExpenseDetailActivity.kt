package com.st10028058.prog7313_part2.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.databinding.ActivityExpenseDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getIntExtra("expense_id", -1)
        if (id == -1) {
            Toast.makeText(this, "Invalid expense", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val dao = AppDatabase.getDatabase(this).expenseDao()

        CoroutineScope(Dispatchers.IO).launch {
            val expense = dao.getExpenseById(id)
            runOnUiThread {
                if (expense == null) {
                    Toast.makeText(this@ExpenseDetailActivity, "Expense not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return@runOnUiThread
                }

                binding.tvDate.text = expense.date
                binding.tvDescription.text = expense.description
                binding.tvCategory.text = expense.category
                binding.tvAmount.text = String.format("R %.2f", expense.amount)

                if (!expense.photoPath.isNullOrEmpty()) {
                    binding.imgPhoto.setImageURI(Uri.parse(expense.photoPath))
                }

                binding.btnDelete.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        dao.deleteExpense(expense)
                        runOnUiThread {
                            Toast.makeText(this@ExpenseDetailActivity, "Expense deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                binding.btnBack.setOnClickListener {
                    finish()
                }
            }
        }
    }
}
