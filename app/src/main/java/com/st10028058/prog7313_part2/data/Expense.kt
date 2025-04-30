package com.st10028058.prog7313_part2.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val description: String,
    val category: String,
    val amount: Double,
    val photoPath: String?
)