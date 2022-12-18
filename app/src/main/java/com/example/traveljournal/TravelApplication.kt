package com.example.traveljournal

import android.app.Application
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.packing.PackingItemRepository

class TravelApplication : Application() {
    private val database by lazy { LocalDB.getPackingInstance(this) }
    val repository by lazy { PackingItemRepository(database.getPackingDAO()) }
}