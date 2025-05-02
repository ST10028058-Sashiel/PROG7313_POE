package com.st10028058.prog7313_part2.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st10028058.prog7313_part2.R
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ItemExpenseBinding
import java.io.File

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = listOf<Expense>()

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
            if (!expense.photoPath.isNullOrEmpty()) {
                val imageFile = File(expense.photoPath)
                if (imageFile.exists()) {
                    imgExpense.setImageURI(Uri.fromFile(imageFile))
                } else {
                    imgExpense.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                imgExpense.setImageResource(android.R.drawable.ic_menu_gallery)
            }


            root.setOnClickListener {
                onItemClick.invoke(expense)
            }

            imgExpense.setOnClickListener {
                onItemClick.invoke(expense)
            }
        }
    }

    fun submitList(list: List<Expense>) {
        expenses = list
        notifyDataSetChanged()
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
