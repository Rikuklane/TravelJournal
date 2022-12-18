package com.example.traveljournal.ui.packing

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.databinding.SinglePackingBinding
import com.example.traveljournal.room.packing.PackingItem

class PackingItemViewHolder(
    private val binding: SinglePackingBinding,
): RecyclerView.ViewHolder(binding.root)
{
    fun bindPackingItem(packingItem: PackingItem, clickListener: PackingClickListener)
    {
        binding.name.text = packingItem.item
        binding.completeButton.setOnClickListener {
            clickListener.completeTaskItem(packingItem)
        }
    }
}
