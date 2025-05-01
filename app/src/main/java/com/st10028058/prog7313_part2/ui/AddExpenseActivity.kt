package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ActivityAddExpenseBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.activity.result.contract.ActivityResultContracts


class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedPhotoUri = uri
                Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
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
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etDate.setText(formatted)
            },
            year, month, day
        )

        datePicker.show()
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

        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            db.expenseDao().insertExpense(
                Expense(
                    date = date,
                    description = description,
                    category = category,
                    amount = amount,
                    photoPath = selectedPhotoUri?.toString()
                )
            )
            Toast.makeText(this@AddExpenseActivity, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
