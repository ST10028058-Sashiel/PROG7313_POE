package com.st10028058.prog7313_part2.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM Expense WHERE userId = :userId ORDER BY date DESC")
    fun getUserExpenses(userId: String): LiveData<List<Expense>>

    @Query("SELECT * FROM Expense WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getExpensesInRange(userId: String, startDate: String, endDate: String): List<Expense>

    @Query("SELECT category, SUM(amount) AS total FROM Expense WHERE userId = :userId AND date BETWEEN :start AND :end GROUP BY category")
    fun getCategoryTotals(userId: String, start: String, end: String): List<CategoryTotal>

    @Query("SELECT * FROM Expense WHERE id = :expenseId LIMIT 1")
    fun getExpenseById(expenseId: Int): Expense?

    @Delete
    suspend fun deleteExpense(expense: Expense)
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