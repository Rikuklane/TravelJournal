package com.example.traveljournal.ui.packing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.databinding.SinglePackingBinding
import com.example.traveljournal.room.packing.PackingItem

class PackingItemAdapter(
    private val packingItems: List<PackingItem>,
    private val clickListener: PackingClickListener
    ): RecyclerView.Adapter<PackingItemViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackingItemViewHolder {
            val from = LayoutInflater.from(parent.context)
            val binding = SinglePackingBinding.inflate(from, parent, false)
            return PackingItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PackingItemViewHolder, position: Int) {
            holder.bindPackingItem(packingItems[position], clickListener)
        }

        override fun getItemCount(): Int = packingItems.size
}