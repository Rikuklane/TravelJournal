package com.example.traveljournal.room.trips

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var country: String?,
    var dates: String?,
    var summary: String?,
)
