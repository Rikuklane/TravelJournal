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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class TripsAdapter(
    var data: Array<TripEntity> = arrayOf(),
    private var listener: TripClickListener
) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    fun interface TripClickListener {
        fun onTripClick(trip: TripEntity)
    }

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = data[position]

        if (trip.images != "") {
            val image = trip.images?.split(",")!!
            CoroutineScope(Dispatchers.IO).launch {
                val bmp = BitmapFactory.decodeFile(image[0])
                withContext(Dispatchers.Main) {
                    holder.itemView.apply {
                        this.findViewById<ImageView>(R.id.countryImageView).setImageBitmap(bmp)
                    }
                }
            }
        }

        holder.itemView.apply {
            val formatter = SimpleDateFormat("dd.MMM yyyy")
            this.findViewById<TextView>(R.id.docTypeTextView).text = trip.country
            this.findViewById<TextView>(R.id.docExpirationTextView).text =
                trip.dateFrom?.let { formatter.format(it) }
            this.findViewById<TextView>(R.id.docExpirationTextView2).text =
                trip.dateTo?.let { formatter.format(it) }
            setOnClickListener { listener.onTripClick(trip) }
        }

    }
}
