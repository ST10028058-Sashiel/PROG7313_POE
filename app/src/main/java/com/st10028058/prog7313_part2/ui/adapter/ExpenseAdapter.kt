package com.st10028058.prog7313_part2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit,
    private val onItemDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = mutableListOf<Expense>()

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun getItemCount(): Int = expenses.size

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.apply {
            tvDescription.text = expense.description
            tvCategory.text = expense.category
            tvAmount.text = String.format("R %.2f", expense.amount)

            if (!expense.photoPath.isNullOrBlank()) {
                Glide.with(imgExpense.context)
                    .load(expense.photoPath)
                    .into(imgExpense)
            } else {
                imgExpense.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            root.setOnClickListener { onItemClick(expense) }
            btnDelete.setOnClickListener { onItemDelete(expense) }
        }
    }

    fun submitList(list: List<Expense>) {
        expenses.clear()
        expenses.addAll(list)
        notifyDataSetChanged()
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