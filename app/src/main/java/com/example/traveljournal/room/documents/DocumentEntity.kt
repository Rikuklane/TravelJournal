package com.example.traveljournal.room.documents

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "documents")
data class DocumentEntity (
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    var type: String?,
    var expiration: Date?,
)
{
    // Companion objects are used for static definitions in Kotlin
    companion object { const val DATEFORMAT = "dd/MM/yyyy" }
}