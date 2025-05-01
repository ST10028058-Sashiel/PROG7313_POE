package com.st10028058.prog7313_part2.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st10028058.prog7313_part2.data.Expense
import com.st10028058.prog7313_part2.databinding.ItemExpenseBinding

class ExpenseAdapter(
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private var expenses = listOf<Expense>()

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.tvDescription.text = expense.description
            binding.tvCategory.text = expense.category
            binding.tvAmount.text = String.format("R %.2f", expense.amount)

            if (!expense.photoPath.isNullOrEmpty()) {
                try {
                    binding.imgExpense.setImageURI(Uri.parse(expense.photoPath))
                } catch (e: Exception) {
                    binding.imgExpense.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.imgExpense.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            binding.root.setOnClickListener {
                onItemClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int = expenses.size

    fun submitList(list: List<Expense>) {
        expenses = list
        notifyDataSetChanged()
    }
}
