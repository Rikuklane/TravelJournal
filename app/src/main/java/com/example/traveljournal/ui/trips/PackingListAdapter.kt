package com.example.traveljournal.ui.trips

import android.graphics.Color
import android.widget.Button

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.traveljournal.R

class PackingListAdapter(
    var data: String = ""
) : RecyclerView.Adapter<PackingListAdapter.PackingListHolder>() {
    private lateinit var packingList: List<String>

    inner class PackingListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackingListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.packinglist_item, parent, false)
        return PackingListHolder(view)
    }

    override fun getItemCount(): Int {
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
                button.setOnClickListener {
                    if (button.tag == null || button.tag == 0) {
                        button.setBackgroundColor(button.getContext().getResources().getColor(R.color.red))
                        button.tag = 1
                    } else {
                        button.setBackgroundColor(button.getContext().getResources().getColor(R.color.green_light))
                        button.tag = 0
                    }
                }
            }
        }

    }
}
