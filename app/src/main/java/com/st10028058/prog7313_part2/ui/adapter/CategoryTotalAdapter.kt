package com.st10028058.prog7313_part2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st10028058.prog7313_part2.data.CategoryTotal
import com.st10028058.prog7313_part2.databinding.ItemCategoryTotalBinding

class CategoryTotalAdapter : RecyclerView.Adapter<CategoryTotalAdapter.TotalViewHolder>() {

    private val items = mutableListOf<CategoryTotal>()

    inner class TotalViewHolder(val binding: ItemCategoryTotalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalViewHolder {
        val binding = ItemCategoryTotalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TotalViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvCategory.text = item.category
        holder.binding.tvTotal.text = String.format("R %.2f", item.total)
    }

    override fun getItemCount() = items.size

    fun submitList(data: List<CategoryTotal>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
}
