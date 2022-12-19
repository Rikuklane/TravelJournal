package com.example.traveljournal.ui.documents

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R
import com.example.traveljournal.room.documents.DocumentEntity
import com.example.traveljournal.room.trips.TripEntity
import com.example.traveljournal.ui.trips.TripsAdapter
import java.text.SimpleDateFormat

class DocumentsAdapter(var data: Array<DocumentEntity> = arrayOf(), private var listener: DocumentsAdapter.DocumentClickListener)
    : RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder>() {

    fun interface DocumentClickListener{
        fun onDocumentClick(document: DocumentEntity)
    }
    inner class DocumentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentsAdapter.DocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: DocumentsAdapter.DocumentViewHolder, position: Int) {
        val document = data[position]

        holder.itemView.apply {
            val formatter = SimpleDateFormat("dd.MMM yyyy")
            this.findViewById<TextView>(R.id.docTypeTextView).text = document.type
            this.findViewById<TextView>(R.id.docExpirationTextView).text = document.expiration?.let {
                formatter.format(
                    it
                )
            }
            setOnClickListener { listener.onDocumentClick(document) }
        }

    }
}