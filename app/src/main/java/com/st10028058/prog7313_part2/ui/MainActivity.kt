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
    private val adapter = ExpenseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(this)

        db.expenseDao().getAllExpenses().observe(this) {
            adapter.submitList(it)
        }

        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }
}
