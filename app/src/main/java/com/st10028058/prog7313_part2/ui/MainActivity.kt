package com.st10028058.prog7313_part2.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.st10028058.prog7313_part2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Buttons
        binding.btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.btnSetGoals.setOnClickListener {
            startActivity(Intent(this, SetGoalActivity::class.java))
        }

        binding.btnFilterExpenses.setOnClickListener {
            startActivity(Intent(this, FilterExpensesActivity::class.java))
        }

        binding.btnCategoryTotals.setOnClickListener {
            startActivity(Intent(this, CategoryTotalActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val user = auth.currentUser
        val email = user?.email ?: "Guest"
        binding.tvWelcome.text = "Welcome back, $email!"

        val prefs = getSharedPreferences("budget_prefs_${user?.uid}", Context.MODE_PRIVATE)
        val min = prefs.getFloat("min_goal", -1f)
        val max = prefs.getFloat("max_goal", -1f)

        binding.tvBudgetGoals.text = if (min == -1f || max == -1f) {
            "Monthly Budget Goals: Not Set"
        } else {
            "Monthly Budget Goals:\nMin: R%.2f | Max: R%.2f".format(min, max)
        }
    }
}
