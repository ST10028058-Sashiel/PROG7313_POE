package com.st10028058.prog7313_part2.ui

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.st10028058.prog7313_part2.databinding.ActivitySetGoalBinding

class SetGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val prefs = getSharedPreferences("budget_prefs_$userId", Context.MODE_PRIVATE)

        // Load saved values
        val minGoal = prefs.getFloat("min_goal", -1f)
        val maxGoal = prefs.getFloat("max_goal", -1f)

        if (minGoal != -1f) binding.etMinGoal.setText(minGoal.toString())
        if (maxGoal != -1f) binding.etMaxGoal.setText(maxGoal.toString())

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
