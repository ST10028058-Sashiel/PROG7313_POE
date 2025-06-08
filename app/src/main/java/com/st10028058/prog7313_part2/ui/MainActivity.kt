package com.st10028058.prog7313_part2.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.R
import com.st10028058.prog7313_part2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private var expenseListener: ListenerRegistration? = null
    private val REQUEST_PERMISSIONS = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAllPermissions()
        createNotificationChannel()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.btnCategoryGraph.setOnClickListener {
            startActivity(Intent(this, SpendingGraphActivity::class.java))
        }

        binding.btnViewMonthlyProgress.setOnClickListener {
            startActivity(Intent(this, MonthlySpendingActivity::class.java))
        }

        binding.btnExport.setOnClickListener {
            startActivity(Intent(this, ExportActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setupBudgetText()
        startExpenseAlerts()
    }

    override fun onPause() {
        super.onPause()
        expenseListener?.remove()
    }

    private fun setupBudgetText() {
        val user = auth.currentUser
        val email = user?.email ?: "Guest"
        binding.tvWelcome.text = "Welcome back, $email!"

        user?.uid?.let { uid ->
            db.collection("goals").document(uid).get()
                .addOnSuccessListener { doc ->
                    val min = doc.getDouble("min_goal") ?: 0.0
                    val max = doc.getDouble("max_goal") ?: 0.0
                    binding.tvBudgetGoals.text = if (min == 0.0 && max == 0.0) {
                        "Monthly Budget Goals: Not Set"
                    } else {
                        "Monthly Budget Goals:\nMin: R%.2f | Max: R%.2f".format(min, max)
                    }
                }
                .addOnFailureListener {
                    binding.tvBudgetGoals.text = "Monthly Budget Goals: Not Set"
                }
        }
    }

    private fun startExpenseAlerts() {
        val uid = auth.currentUser?.uid.orEmpty()
        db.collection("goals").document(uid).get()
            .addOnSuccessListener { doc ->
                val minGoal = doc.getDouble("min_goal") ?: 0.0
                val maxGoal = doc.getDouble("max_goal") ?: 0.0

                expenseListener?.remove()
                expenseListener = db.collection("expenses")
                    .whereEqualTo("userId", uid)
                    .addSnapshotListener { snaps, _ ->
                        val total = snaps?.sumOf { it.getDouble("amount") ?: 0.0 } ?: 0.0
                        evaluateBudget(total, minGoal, maxGoal)
                    }
            }
    }

    private fun evaluateBudget(total: Double, min: Double, max: Double) {
        when {
            max > 0 && total >= max -> sendNotification(
                "🚨 Over Budget!",
                "You've spent R%.2f (max R%.2f)".format(total, max)
            )
            min > 0 && total >= min -> sendNotification(
                "✅ Budget Milestone",
                "You've reached your min goal: R%.2f".format(total)
            )
        }
    }

    private fun sendNotification(title: String, msg: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pending = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "budget_channel")
            .setSmallIcon(R.drawable.ic_alert)
            .setContentTitle(title)
            .setContentText(msg)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pending)

        NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "budget_channel",
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notify when budget limits are reached"
            }

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA)
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }
}
