package com.example.traveljournal.ui.trips

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentTripsBinding
import com.example.traveljournal.room.trips.TripEntity


class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private lateinit var tripsAdapter: TripsAdapter
    private val model: TripViewModel by viewModels()
    private val TAG = "TripsFragment"

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

        checkPermissionsAndSetupRecyclerView()
        binding.buttonNewTrip.setOnClickListener{ openNewTripFragment() }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        model.refresh(tripsAdapter)
        tripsAdapter.data = model.tripArray
        // TODO check if necessary: tripsAdapter.notifyDataSetChanged()
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
     * Activity change: opens trip details view
     */
    private fun openTripDetailsFragment(trip: TripEntity) {
        val bundle = Bundle()
        bundle.putLong(TripDetailsFragment.EXTRA_TRIP_ID, trip.id)
        findNavController().navigate(R.id.action_openTripDetailsFragment, bundle)
    }

    /**
     * Activity change: allows the user to enter a new trip to the database
     */
    private fun openNewTripFragment() {
        findNavController().navigate(R.id.action_openNewTripFragment)
    }

    private fun checkPermissionsAndSetupRecyclerView(){
        if (checkStoragePermission()) {
            setupRecyclerView()
        } else{
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission() =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED)
        }

    private fun requestStoragePermission() =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "Storage permissions granted")
            setupRecyclerView()
        } else {
            Toast.makeText(context, "Can't load the images without external storage permissions!", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Can't see the trips without external storage permissions!")
        }
    }
}