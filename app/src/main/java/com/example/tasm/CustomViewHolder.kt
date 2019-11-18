package com.example.tasm

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class CustomViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    val prime: TextView = view.prime_number
}