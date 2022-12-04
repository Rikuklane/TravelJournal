package com.example.traveljournal.ui.trips

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.traveljournal.databinding.FragmentTripDetailsBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import java.io.File

class TripDetailsFragment : Fragment() {

    companion object { const val EXTRA_TRIP_ID = "tripID" }
    private var _binding: FragmentTripDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //get trip ID and show it in the UI
        loadAndShowTrip()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadAndShowTrip() {
        // Get trip ID from intent, load trip details from DB and show it in the UI
        val id = activity?.intent?.getIntExtra(EXTRA_TRIP_ID, -1)

        val loadedTrip = id?.let { getTripFromDB(it) }
        loadedTrip?.let { showTrip(it) }
    }

    private fun getTripFromDB(id: Int): TripEntity? {
        val tripsInDB = context?.let { LocalDB.getInstance(it).getTripDAO().loadTrips() }
        if (tripsInDB != null) {
            return tripsInDB.get(id-1)
        }
        return null
    }

    private fun showTrip(trip: TripEntity) {
        val bmp = BitmapFactory.decodeFile(context?.let { getFile(it, trip.country.toString()) })
        trip.apply {
            //TODO: create an xml file and assign values to textviews etc
            binding.detailsCountryTextView.text = country
            binding.detailsTripDates.text = dates
            binding.detailsTripSummary.text = summary
            binding.detailsImageView.setImageBitmap(bmp)
        }
    }

    private fun getFile(context: Context, fileName: String): String {
        val mediaStorageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("TAG", "failed to create directory")
        }
        return mediaStorageDir.path + File.separator + fileName + ".jpg"
    }
}