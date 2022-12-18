package com.example.traveljournal.room.packing

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface PackingDAO {

    @Query("SELECT * FROM packingItems")
    fun allPackingItems(): Flow<List<PackingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPacking(packingItem: PackingItem)
    @Delete
    fun deletePacking(packingItem: PackingItem)

}