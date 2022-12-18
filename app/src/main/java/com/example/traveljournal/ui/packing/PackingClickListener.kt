package com.example.traveljournal.ui.packing

import com.example.traveljournal.room.packing.PackingItem

interface PackingClickListener
{
    fun completeTaskItem(packingItem: PackingItem)
}