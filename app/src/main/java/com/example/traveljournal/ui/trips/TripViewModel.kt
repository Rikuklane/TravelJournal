package com.example.traveljournal.ui.trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity

class TripViewModel(val app: Application) : AndroidViewModel(app) {
    //placeholder data
    var tripArray: Array<TripEntity> = arrayOf(
        TripEntity(1, "France", "10.10.2022-20.10.2022", "Haven't been there yet"),
        TripEntity(2, "Turkey", "10.01.2021-15.01.2021", "It was a really cool trip"),
    )

    /**
     * Reload dataset from DB, put it in in-memory list
     */
    fun refresh() {
        val db = LocalDB.getInstance(app)
        val trips = db.getTripDAO().loadTrips()
        tripArray = trips
    }
}
