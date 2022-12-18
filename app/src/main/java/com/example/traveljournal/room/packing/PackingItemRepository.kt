package com.example.traveljournal.room.packing

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class PackingItemRepository(private val PackingDAO: PackingDAO){
    val allPackingItems: Flow<List<PackingItem>> = PackingDAO.allPackingItems()

    @WorkerThread
    suspend fun insertPackingItem(packingItem: PackingItem) {
        PackingDAO.insertPacking(packingItem)
    }

    @WorkerThread
    suspend fun deletePackingItem(packingItem: PackingItem) {
        PackingDAO.deletePacking(packingItem)
    }
}

