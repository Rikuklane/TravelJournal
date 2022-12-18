package com.example.traveljournal.ui.trips

import android.widget.Button

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class PackingListAdapter(
    var data: String = ""
) : RecyclerView.Adapter<PackingListAdapter.PackingListHolder>() {
    private lateinit var packingList : List<String>

    inner class PackingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackingListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.packinglist_item, parent, false)
        return PackingListHolder(view)
    }

    override fun getItemCount() : Int{
        packingList = data.split("\n")
        return packingList.size
    }


    override fun onBindViewHolder(holder: PackingListHolder, position: Int) {
        var packingListItems = itemCount
        val item = packingList[position]

        if (item != "") {
            holder.itemView.apply {
                val button: Button = this.findViewById(R.id.tripPackingList_item)
                button.text = item
                button.setOnClickListener { button.setBackgroundColor(140) }
            }
        }

    }

    fun onPackinglistitemClick(){

    }
}
