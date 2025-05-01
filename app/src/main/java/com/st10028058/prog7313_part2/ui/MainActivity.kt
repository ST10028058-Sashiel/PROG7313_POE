package com.st10028058.prog7313_part2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.st10028058.prog7313_part2.data.AppDatabase
import com.st10028058.prog7313_part2.databinding.ActivityMainBinding
import com.st10028058.prog7313_part2.ui.adapter.ExpenseAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter = ExpenseAdapter { expense ->
        val intent = Intent(this, ExpenseDetailActivity::class.java)
        intent.putExtra("expense_id", expense.id)
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        // ✅ Add navigation buttons
        binding.btnSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalActivity::class.java))
        }

        binding.btnFilterExpenses.setOnClickListener {
            startActivity(Intent(this, FilterExpensesActivity::class.java))
        }

        binding.btnCategoryTotals.setOnClickListener {
            startActivity(Intent(this, CategoryTotalActivity::class.java))
        }


    }
}
