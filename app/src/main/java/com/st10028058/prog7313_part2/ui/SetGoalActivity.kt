package com.st10028058.prog7313_part2.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.st10028058.prog7313_part2.databinding.ActivitySetGoalBinding

class SetGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)

        // Load saved values
        val minGoal = prefs.getFloat("min_goal", 0f)
        val maxGoal = prefs.getFloat("max_goal", 0f)

        binding.etMinGoal.setText(minGoal.toString())
        binding.etMaxGoal.setText(maxGoal.toString())

        // Save on click
        binding.btnSaveGoals.setOnClickListener {
            val min = binding.etMinGoal.text.toString().toFloatOrNull()
            val max = binding.etMaxGoal.text.toString().toFloatOrNull()

            if (min != null && max != null && min <= max) {
                prefs.edit()
                    .putFloat("min_goal", min)
                    .putFloat("max_goal", max)
                    .apply()
                Toast.makeText(this, "Goals saved!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Invalid input. Min must be ≤ Max.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
