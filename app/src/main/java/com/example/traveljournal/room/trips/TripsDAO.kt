package com.example.traveljournal.room.trips

import androidx.room.*

@Dao
interface TripsDAO {

    @Query("SELECT * FROM trips")
    fun loadTrips(): Array<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrips(vararg trips: TripEntity)

    @Delete
    fun deleteTrips(vararg trips: TripEntity)

}