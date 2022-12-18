package com.example.traveljournal.ui.trips


import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class TripGalleryAdapter(
    var data: List<String> = mutableListOf()
) : RecyclerView.Adapter<TripGalleryAdapter.TripGalleryPicHolder>() {

    inner class TripGalleryPicHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripGalleryPicHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_pic, parent, false)
        return TripGalleryPicHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TripGalleryPicHolder, position: Int) {
        val img = data[position].strip()

        if (img != "") {
            val bmp = BitmapFactory.decodeFile(img)
            holder.itemView.apply {
                this.findViewById<ImageView>(R.id.tripImage).setImageBitmap(bmp)
            }
        }

    }
}
