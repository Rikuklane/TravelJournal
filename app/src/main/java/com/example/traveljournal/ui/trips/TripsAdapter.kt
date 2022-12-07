package com.example.traveljournal.ui.trips

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.room.trips.TripEntity

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

        if (trip.image != "") {
            val bmp = BitmapFactory.decodeFile(trip.image)
            holder.itemView.apply {
                this.findViewById<ImageView>(R.id.countryImageView).setImageBitmap(bmp)
            }
        }

        holder.itemView.apply {
            this.findViewById<TextView>(R.id.countryTextView).text = trip.country
            this.findViewById<TextView>(R.id.datesTextView).text = trip.dateFrom.toString()
            this.findViewById<TextView>(R.id.datesTextView).text = trip.dateTo.toString() // TODO add field to xml to hold 2 dates
            setOnClickListener { listener.onTripClick(trip) }
        }

    }
}
