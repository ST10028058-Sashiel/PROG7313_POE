package com.st10028058.prog7313_part2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.launch

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()

        if (date.isEmpty() || time.isEmpty() || description.isEmpty() || category.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Enter a valid number for amount", Toast.LENGTH_SHORT).show()
            return
        }

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.expenseDao().insertExpense(
                Expense(
                    date = date,
                    time = time,
                    description = description,
                    category = category,
                    amount = amount,
                    photoPath = null // not used in this version
                )
            )
            Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
