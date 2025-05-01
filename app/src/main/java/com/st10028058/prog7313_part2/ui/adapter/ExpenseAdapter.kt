package com.st10028058.prog7313_part2.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st10028058.prog7313_part2.R
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ItemExpenseBinding

class ExpenseAdapter : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = listOf<Expense>()

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

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
            tvAmount.text = "R${expense.amount}"

            if (!expense.photoPath.isNullOrEmpty()) {
                imgExpense.setImageURI(Uri.parse(expense.photoPath))
            } else {
                imgExpense.setImageResource(android.R.drawable.ic_menu_gallery)
                // Optional fallback
            }
        }
    }


    fun submitList(list: List<Expense>) {
        expenses = list
        notifyDataSetChanged()
    }
}