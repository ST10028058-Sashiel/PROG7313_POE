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
