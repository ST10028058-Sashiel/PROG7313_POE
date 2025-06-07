package com.st10028058.prog7313_part2.data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.st10028058.prog7313_part2.data.Expense
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object ExpenseRepository {

    private val firestore = Firebase.firestore
    private val expensesCollection = firestore.collection("expenses")

    fun addExpense(expense: Expense, onResult: (Boolean) -> Unit) {
        val docRef = expensesCollection.document()
        expense.id = docRef.id
        docRef.set(expense)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getExpenses(userId: String, onComplete: (List<Expense>) -> Unit) {
        expensesCollection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val expenses = result.toObjects(Expense::class.java)
                onComplete(expenses)
            }
    }

    fun deleteExpense(id: String, onResult: (Boolean) -> Unit) {
        expensesCollection.document(id).delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateExpense(expense: Expense, onResult: (Boolean) -> Unit) {
        expensesCollection.document(expense.id).set(expense)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
