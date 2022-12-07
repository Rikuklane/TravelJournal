package com.example.traveljournal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.traveljournal.room.trips.DateTypeConverter
import com.example.traveljournal.room.trips.TripEntity
import com.example.traveljournal.room.trips.TripsDAO

@TypeConverters(DateTypeConverter::class)
@Database(entities = [ TripEntity::class ], version = 1, exportSchema = false)
abstract class LocalDB : RoomDatabase() {
    companion object {
        private lateinit var TripDB : LocalDB

        @Synchronized
        fun getInstance(context: Context) : LocalDB {
            if (!this::TripDB.isInitialized) {
                TripDB = Room.databaseBuilder(
                    context, LocalDB::class.java, "myTrips")
                    .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                    .allowMainThreadQueries() // if possible, use background thread instead
                    .build()
            }
            return TripDB
        }
    }

    abstract fun getTripDAO(): TripsDAO

}