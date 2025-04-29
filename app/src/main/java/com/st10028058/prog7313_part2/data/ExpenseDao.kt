package com.st10028058.prog7313_part2.data


import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM Expense ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>
}