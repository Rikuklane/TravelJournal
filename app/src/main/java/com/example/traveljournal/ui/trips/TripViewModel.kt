package com.example.traveljournal.ui.trips

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity

class TripViewModel(val app: Application) : AndroidViewModel(app) {
    private val TAG = "TripViewModel"
    var tripArray: Array<TripEntity> = arrayOf(
    )

    /**
     * Reload dataset from DB, put it in in-memory list
     */
    @SuppressLint("NotifyDataSetChanged")
    fun refresh(tripsAdapter: TripsAdapter) {
        val db = LocalDB.getInstance(app)
        try {
            tripArray = db.getTripDAO().loadTrips()
            tripsAdapter.notifyDataSetChanged()
            Log.i(TAG, "Trips refreshed")
        } catch (exception: Exception){
            Log.i(TAG, "Error refreshing trips: $exception")
        }
    }
}
