package com.example.traveljournal.room.trips

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {
    @TypeConverter
    fun toDate(date: Long) = Date(date)

    @TypeConverter
    fun fromDate(date: Date?) = date?.time ?: Calendar.getInstance().timeInMillis
}

