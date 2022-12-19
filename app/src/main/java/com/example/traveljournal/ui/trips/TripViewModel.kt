package com.example.traveljournal.ui.trips

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
        val db = LocalDB.getTripsInstance(app)
        try {
            tripArray = db.getTripDAO().loadTrips()
            tripsAdapter.notifyDataSetChanged()
            Log.i(TAG, "Trips refreshed")
        } catch (exception: Exception){
            Log.i(TAG, "Error refreshing trips: $exception")
        }
    }

    fun loadImage(image: Uri): Bitmap {
        getApplication<Application>().contentResolver.openInputStream(image).use {
            val fullBitmap = BitmapFactory.decodeStream(it)
            val ratio = fullBitmap.width.toDouble() / fullBitmap.height
            return Bitmap.createScaledBitmap(fullBitmap, (800 * ratio).toInt(), 800, false)
        }
    }
}
