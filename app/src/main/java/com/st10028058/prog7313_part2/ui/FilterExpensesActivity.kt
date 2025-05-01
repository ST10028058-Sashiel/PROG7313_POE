package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.databinding.ActivityFilterExpensesBinding
import com.st10028058.prog7313_part2.ui.adapter.ExpenseAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class FilterExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterExpensesBinding
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExpenseAdapter { expense ->
            val intent = Intent(this, ExpenseDetailActivity::class.java)
            intent.putExtra("expense_id", expense.id)
            startActivity(intent)
        }

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
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dao = AppDatabase.getDatabase(this).expenseDao()
            CoroutineScope(Dispatchers.IO).launch {
                val expenses = dao.getExpensesInRange(startDate, endDate)
                runOnUiThread {
                    adapter.submitList(expenses)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
