package com.st10028058.prog7313_part2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.databinding.ActivitySetGoalBinding

class SetGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalBinding
    private val db = Firebase.firestore

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

        // Load from Firestore
        db.collection("goals").document(userId).get()
            .addOnSuccessListener { doc ->
                val minGoal = doc.getDouble("min_goal")?.toFloat()
                val maxGoal = doc.getDouble("max_goal")?.toFloat()

                if (minGoal != null) binding.etMinGoal.setText(minGoal.toString())
                if (maxGoal != null) binding.etMaxGoal.setText(maxGoal.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load goals", Toast.LENGTH_SHORT).show()
            }

        // Save to Firestore
        binding.btnSaveGoals.setOnClickListener {
            val min = binding.etMinGoal.text.toString().toFloatOrNull()
            val max = binding.etMaxGoal.text.toString().toFloatOrNull()

            if (min != null && max != null && min <= max) {
                val goalData = mapOf(
                    "min_goal" to min,
                    "max_goal" to max
                )

                db.collection("goals").document(userId).set(goalData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Goals saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save goals", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Invalid input. Min must be ≤ Max.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

//Code Attribution

//# Code and support generated with the help of OpenAI's ChatGPT.
//ChatGPT (OpenAI GPT-4)

//Kotlin Official Documentation
//Code patterns and language features were referenced from the official Kotlin documentation, including examples related to classes, coroutines, and Android development.
//Source:
//JetBrains, 2025. Kotlin Programming Language Documentation [online] Available at: https://kotlinlang.org/docs/home.html [Accessed 2 May 2025].

// W3schools / /https://www.w3schools.com/kotlin/index.php

//Stack Overflow
//Practical coding solutions for common errors and Firebase integration were adapted from community answers on Stack Overflow.
//Source:
//Stack Overflow, 2025. How to implement Firebase login in Kotlin? [online] Available at: https://stackoverflow.com [Accessed 2 May 2025].

//Firebase Documentation (by Google)
//The Firebase Authentication, Realtime Database, and Firestore implementation guides were followed to correctly integrate user authentication and data handling.
//Source:
//Google, 2025. Firebase Documentation – Android Authentication [online] Available at: https://firebase.google.com/docs/auth/android/start [Accessed 2 May 2025].