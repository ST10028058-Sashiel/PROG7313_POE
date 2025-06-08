package com.st10028058.prog7313_part2.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.databinding.ActivityMonthlySpendingBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MonthlySpendingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthlySpendingBinding
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val sdfYearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var minGoal = 0.0
    private var maxGoal = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlySpendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvMonthlyStatus.text = "Tap Refresh to show monthly progress"
        binding.btnRefresh.setOnClickListener {
            binding.tvMonthlyStatus.text = "Checking..."
            binding.btnRefresh.isEnabled = false
            loadGoalsThenSpending()


        }
        binding.btnBackDashboard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun loadGoalsThenSpending() {
        db.collection("goals").document(userId).get()
            .addOnSuccessListener { doc ->
                minGoal = doc.getDouble("min_goal") ?: 0.0
                maxGoal = doc.getDouble("max_goal") ?: 0.0
                fetchAllExpenses()
            }
            .addOnFailureListener { e ->
                Log.e("MonthlySpending", "Error loading goals", e)
                binding.tvMonthlyStatus.text = "Failed to load goals."
                binding.btnRefresh.isEnabled = true
            }
    }

    private fun fetchAllExpenses() {
        val now = Calendar.getInstance()
        val thisMonth = sdfYearMonth.format(now.time) // e.g., "2025-06"

        db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                // Filter client-side by month prefix
                val total = snapshot
                    .mapNotNull { doc ->
                        val dateStr = doc.getString("date") ?: return@mapNotNull null
                        if (dateStr.startsWith(thisMonth)) {
                            doc.getDouble("amount") ?: 0.0
                        } else null
                    }
                    .sum()
                updateUI(total)
                binding.btnRefresh.isEnabled = true
            }
            .addOnFailureListener { e ->
                Log.e("MonthlySpending", "Error retrieving expenses", e)
                binding.tvMonthlyStatus.text = "Error retrieving expenses."
                binding.btnRefresh.isEnabled = true
            }
    }

    private fun updateUI(total: Double) {
        binding.progressMonthly.max = 100
        when {
            minGoal <= 0 || maxGoal <= 0 || minGoal >= maxGoal -> {
                binding.progressMonthly.progress = 0
                binding.progressMonthly.progressDrawable.setTint(Color.GRAY)
                binding.tvMonthlyStatus.text = "Set valid goals to track."
            }
            total < minGoal -> {
                val pct = ((total / minGoal) * 50).roundToInt().coerceIn(0, 50)
                binding.progressMonthly.progress = pct
                binding.progressMonthly.progressDrawable.setTint(Color.YELLOW)
                binding.tvMonthlyStatus.text = "Below minimum: R%.2f / R%.2f".format(total, minGoal)
            }
            total > maxGoal -> {
                binding.progressMonthly.progress = 100
                binding.progressMonthly.progressDrawable.setTint(Color.RED)
                binding.tvMonthlyStatus.text = "Exceeded maximum: R%.2f / R%.2f".format(total, maxGoal)
            }
            else -> {
                val pct = ((total - minGoal) / (maxGoal - minGoal) * 100).roundToInt().coerceIn(0, 100)
                binding.progressMonthly.progress = pct
                binding.progressMonthly.progressDrawable.setTint(Color.GREEN)
                binding.tvMonthlyStatus.text = "On track: R%.2f / R%.2f–R%.2f".format(total, minGoal, maxGoal)
            }
        }
    }
}
