package com.st10028058.prog7313_part2.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.databinding.ActivitySpendingGraphBinding
import java.text.SimpleDateFormat
import java.util.*

class SpendingGraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpendingGraphBinding
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpendingGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etStartDate.setOnClickListener { showDatePicker(true) }
        binding.etEndDate.setOnClickListener { showDatePicker(false) }

        binding.btnShowGraph.setOnClickListener {
            if (startDate.isBlank() || endDate.isBlank()) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loadGoalsAndSpending()
        }

        binding.btnBackDashboard.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,
            { _, year, month, day ->
                val date = String.format("%04d-%02d-%02d", year, month + 1, day)
                if (isStart) {
                    startDate = date
                    binding.etStartDate.setText(date)
                } else {
                    endDate = date
                    binding.etEndDate.setText(date)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun loadGoalsAndSpending() {
        db.collection("goals").document(userId).get()
            .addOnSuccessListener { doc ->
                val minGoal = doc.getDouble("min_goal") ?: 0.0
                val maxGoal = doc.getDouble("max_goal") ?: 0.0
                loadSpendingData(minGoal, maxGoal)
            }
            .addOnFailureListener {
                loadSpendingData(0.0, 0.0)
            }
    }

    private fun loadSpendingData(minGoal: Double, maxGoal: Double) {
        db.collection("expenses")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val categoryTotals = mutableMapOf<String, Double>()
                var totalMax = 0.0

                querySnapshot.forEach { doc ->
                    val date = doc.getString("date") ?: return@forEach
                    if (date >= startDate && date <= endDate) {
                        val category = doc.getString("category") ?: return@forEach
                        val amount = doc.getDouble("amount") ?: 0.0
                        categoryTotals[category] = categoryTotals.getOrDefault(category, 0.0) + amount
                        totalMax = maxOf(totalMax, categoryTotals[category] ?: 0.0)
                    }
                }

                displayGraph(categoryTotals, totalMax, minGoal, maxGoal)
            }
    }

    private fun displayGraph(
        totals: Map<String, Double>,
        maxAmount: Double,
        minGoal: Double,
        maxGoal: Double
    ) {
        binding.chartContainer.removeAllViews()

        if (minGoal > 0.0) {
            binding.chartContainer.addView(goalBar("Min Goal", minGoal, maxAmount, Color.BLUE))
        }
        if (maxGoal > 0.0) {
            binding.chartContainer.addView(goalBar("Max Goal", maxGoal, maxAmount, Color.RED))
        }

        totals.forEach { (category, total) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 12, 0, 12)
            }

            val label = TextView(this).apply {
                text = category
                width = 200
                setTextColor(Color.WHITE) // Light text for dark background
            }

            val percent = (if (maxAmount > 0) total / maxAmount else 0.0).coerceIn(0.0, 1.0)

            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams((percent * 600).toInt(), 40)
                setBackgroundColor(Color.parseColor("#4CAF50"))
            }

            val amount = TextView(this).apply {
                text = "R%.2f".format(total)
                setPadding(16, 0, 0, 0)
                setTextColor(Color.LTGRAY)
            }

            row.addView(label)
            row.addView(bar)
            row.addView(amount)
            binding.chartContainer.addView(row)
        }
    }

    private fun goalBar(label: String, value: Double, maxAmount: Double, color: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 8)

            addView(TextView(context).apply {
                text = label
                width = 200
                setTextColor(color)
            })

            val percent = if (maxAmount > 0) (value / maxAmount).coerceIn(0.0, 1.0) else 0.0
            addView(View(context).apply {
                layoutParams = LinearLayout.LayoutParams((percent * 600).toInt(), 30)
                setBackgroundColor(color)
            })

            addView(TextView(context).apply {
                text = "R%.2f".format(value)
                setPadding(16, 0, 0, 0)
                setTextColor(color)
            })
        }
    }
}
