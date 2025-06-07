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
        holder.binding.tvTotal.text = String.format("R %.2f", item.totalAmount)
    }

    override fun getItemCount() = items.size

    fun submitList(data: List<CategoryTotal>) {
        items.clear()
        items.addAll(data)
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