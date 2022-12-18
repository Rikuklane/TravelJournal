package com.example.traveljournal.ui.trips

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TripsPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(manager!!) {
    private val fragments: MutableList<Fragment> = mutableListOf(PastTripsFragment(), FutureTripsFragment())
    private val fragmentsTitles: MutableList<String> = mutableListOf("Past trips", "Future trips")

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsTitles[position]
    }

}