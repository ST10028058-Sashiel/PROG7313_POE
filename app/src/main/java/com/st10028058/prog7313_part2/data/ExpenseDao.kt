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

