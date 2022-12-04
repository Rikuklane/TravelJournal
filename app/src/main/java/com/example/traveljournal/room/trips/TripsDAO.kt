package com.example.traveljournal.room.trips

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripsDAO {

    @Query("SELECT * FROM trips")
    fun loadTrips(): Array<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrips(vararg recipes: TripEntity)
}