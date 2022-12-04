package com.example.traveljournal.ui.trips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.databinding.FragmentTripsBinding
import com.example.traveljournal.room.trips.TripEntity


class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private lateinit var tripsAdapter: TripsAdapter
    private val model: TripViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        binding.buttonNewTrip.setOnClickListener{ openNewTripFragment() }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        model.refresh()
        tripsAdapter.data = model.tripArray
        tripsAdapter.notifyDataSetChanged() //Updates recipesAdapter
    }

    /**
     * Activity change: opens trip details view
     */
    private fun openTripDetailsFragment(trip: TripEntity) {
        val intent = Intent(context, TripDetailsFragment::class.java)
        intent.putExtra(TripDetailsFragment.EXTRA_TRIP_ID, trip.id)
        startActivity(intent)
    }

    /**
     * sets up a recycler view for  trips
     */
    private fun setupRecyclerView() {
        val tripClickListener = TripsAdapter.TripClickListener { p -> openTripDetailsFragment(p) }
        tripsAdapter = TripsAdapter(model.tripArray, tripClickListener) //Initialize adapter
        binding.recyclerviewTrips.adapter = tripsAdapter //Bind recyclerview to adapter
        binding.recyclerviewTrips.layoutManager = LinearLayoutManager(context) //Gives layout
    }

    /**
     * Activity change: allows the user to enter a new trip to the database
     */
    private fun openNewTripFragment() {
        startActivity(Intent(context, NewTripFragment::class.java))
    }
}