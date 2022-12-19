package com.example.traveljournal.ui.trips

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.traveljournal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class TripsParentFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trips_parent, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = TripsPagerAdapter(childFragmentManager)

        val newButton = view.findViewById<FloatingActionButton>(R.id.button_new_trip)
        newButton.setOnClickListener{ openNewTripFragment() }
    }

    /**
     * Activity change: allows the user to enter a new trip to the database
     */
    private fun openNewTripFragment() {
        findNavController().navigate(R.id.action_openNewTripFragment)
    }

}