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
//Code Attribution
//ChatGPT (OpenAI GPT-4)
//AI-generated code snippets to fix errors and explanations were provided using ChatGPT for Kotlin development tasks, RoomDB setup.
//Source:
//OpenAI, 2024. ChatGPT (version GPT-4) [online] Available at: https://chat.openai.com [Accessed 2 May 2025].

//Kotlin Official Documentation
//Code patterns and language features were referenced from the official Kotlin documentation, including examples related to classes, coroutines, and Android development.
//Source:
//JetBrains, 2025. Kotlin Programming Language Documentation [online] Available at: https://kotlinlang.org/docs/home.html [Accessed 2 May 2025].

//Stack Overflow
//Practical coding solutions for common errors and Firebase integration were adapted from community answers on Stack Overflow.
//Source:
//Stack Overflow, 2025. How to implement Firebase login in Kotlin? [online] Available at: https://stackoverflow.com [Accessed 2 May 2025].

//Firebase Documentation (by Google)
//The Firebase Authentication, Realtime Database, and Firestore implementation guides were followed to correctly integrate user authentication and data handling.
//Source:
//Google, 2025. Firebase Documentation – Android Authentication [online] Available at: https://firebase.google.com/docs/auth/android/start [Accessed 2 May 2025].