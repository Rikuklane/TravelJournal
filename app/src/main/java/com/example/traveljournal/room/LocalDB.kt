package com.example.traveljournal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.traveljournal.room.documents.DocumentEntity
import com.example.traveljournal.room.documents.DocumentsDAO
import com.example.traveljournal.room.packing.PackingDAO
import com.example.traveljournal.room.packing.PackingItem
import com.example.traveljournal.room.trips.DateTypeConverter
import com.example.traveljournal.room.trips.TripEntity
import com.example.traveljournal.room.trips.TripsDAO

@TypeConverters(DateTypeConverter::class)
@Database(entities = [ TripEntity::class , DocumentEntity::class, PackingItem::class], version = 3, exportSchema = false)
abstract class LocalDB : RoomDatabase() {
    companion object {
        private lateinit var TripDB : LocalDB
        private lateinit var PackingDB: LocalDB
        private lateinit var DocumentDB: LocalDB

        @Synchronized
        fun getTripsInstance(context: Context) : LocalDB {
            if (!this::TripDB.isInitialized) {
                TripDB = Room.databaseBuilder(
                    context, LocalDB::class.java, "myTrips")
                    .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                    .allowMainThreadQueries() // if possible, use background thread instead
                    .build()
            }
            return TripDB
        }

        @Synchronized
        fun getPackingInstance(context: Context) : LocalDB {
            if (!this::PackingDB.isInitialized) {
                PackingDB = Room.databaseBuilder(
                    context, LocalDB::class.java, "packingItems")
                    .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                    .allowMainThreadQueries() // if possible, use background thread instead
                    .build()
            }
            return PackingDB
        }

        @Synchronized
        fun getDocumentsInstance(context: Context) : LocalDB {
            if (!this::DocumentDB.isInitialized) {
                DocumentDB = Room.databaseBuilder(
                    context, LocalDB::class.java, "myDocuments")
                    .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                    .allowMainThreadQueries() // if possible, use background thread instead
                    .build()
            }
            return DocumentDB
        }
    }

    abstract fun getTripDAO(): TripsDAO

    abstract fun getDocumentDAO(): DocumentsDAO

    abstract fun getPackingDAO(): PackingDAO

}