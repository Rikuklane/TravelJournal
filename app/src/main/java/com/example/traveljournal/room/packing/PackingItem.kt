package com.example.traveljournal.room.packing

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packingItems")
data class PackingItem(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var item: String,
)
