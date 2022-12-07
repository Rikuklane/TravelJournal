package com.example.traveljournal.room.trips

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "trips")
data class TripEntity (
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var country: String?,
    var summary: String?,
    var dateFrom: Date?,
    var dateTo: Date?,
    var image: String?,
){
    // Companion objects are used for static definitions in Kotlin
    companion object { const val DATEFORMAT = "dd/MM/yyyy" }
}
