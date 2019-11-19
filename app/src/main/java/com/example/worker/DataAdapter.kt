package com.example.worker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter

class DataAdapter : Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item,
                parent,
                false
            )
        )

    override fun getItemCount() = primeNumbers.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.prime.text = primeNumbers[position].toString()
    }

    fun addItem(number: Int) {
        if (!primeNumbers.contains(number)) {
            primeNumbers.add(number)
            notifyItemInserted(primeNumbers.size)
        }
    }

    companion object {
        var primeNumbers = mutableListOf<Int>()
    }
}
