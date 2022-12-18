package com.example.traveljournal.room.trips

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var country: String?,
    var summary: String?,
    var dateFrom: Date?,
    var dateTo: Date?,
    var images: String?,
){
    // Companion objects are used for static definitions in Kotlin
    companion object { const val DATEFORMAT = "dd/MM/yyyy" }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TripEntity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
