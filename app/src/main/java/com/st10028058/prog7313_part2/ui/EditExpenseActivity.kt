package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityEditExpenseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditExpenseBinding
    private var expenseId: Int = -1
    private var photoPath: String? = null

    private val pickImageLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                photoPath = it.toString()
                binding.imgPhoto.setImageURI(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        expenseId = intent.getIntExtra("expense_id", -1)
        val dao = AppDatabase.getDatabase(this).expenseDao()

        CoroutineScope(Dispatchers.IO).launch {
            val expense = dao.getExpenseById(expenseId)
            if (expense != null) {
                runOnUiThread {
                    binding.etDate.setText(expense.date)
                    binding.etDescription.setText(expense.description)
                    binding.etCategory.setText(expense.category)
                    binding.etAmount.setText(expense.amount.toString())
                    photoPath = expense.photoPath
                    if (!photoPath.isNullOrEmpty()) {
                        binding.imgPhoto.setImageURI(Uri.parse(photoPath))
                    }
                }
            }
        }

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this,
                { _, year, month, day ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    binding.etDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.imgPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val date = binding.etDate.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val category = binding.etCategory.text.toString().trim()
            val amount = binding.etAmount.text.toString().trim().toDoubleOrNull()

            if (date.isEmpty() || desc.isEmpty() || category.isEmpty() || amount == null) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedExpense = Expense(
                id = expenseId,
                userId = userId,
                date = date,
                description = desc,
                category = category,
                amount = amount,
                photoPath = photoPath
            )

            CoroutineScope(Dispatchers.IO).launch {
                dao.insertExpense(updatedExpense)
                runOnUiThread {
                    Toast.makeText(this@EditExpenseActivity, "Expense updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
