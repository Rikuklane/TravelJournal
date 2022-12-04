package com.example.traveljournal.ui.trips

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.room.trips.TripEntity
import java.io.File

class TripsAdapter(var data: Array<TripEntity> = arrayOf(), private var listener: TripClickListener)
    :RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    fun interface TripClickListener{
        fun onTripClick(trip: TripEntity)
    }

    inner class TripViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = data[position]

        val bmp = BitmapFactory.decodeFile(getFile(holder.itemView.context, trip.country.toString())) //Creates the image to display

        holder.itemView.apply {
            this.findViewById<TextView>(R.id.countryTextView).text = trip.country
            this.findViewById<TextView>(R.id.datesTextView).text = trip.dates
            this.findViewById<ImageView>(R.id.countryImageView).setImageBitmap(bmp)
            setOnClickListener { listener.onTripClick(trip) }
        }

    }

    //for displaying an image
    private fun getFile(context: Context, fileName: String): String {
        val mediaStorageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("TAG", "failed to create directory")
        }
        return mediaStorageDir.path + File.separator + fileName + ".jpg"
    }
}
