package com.example.traveljournal.ui.trips

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentTripDetailsBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import java.io.File

class TripDetailsFragment : Fragment() {

    companion object { const val EXTRA_TRIP_ID = "tripId" }
    private var _binding: FragmentTripDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //for activity button animation
    private val rotateOpen: Animation by lazy {AnimationUtils.loadAnimation(context, R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy {AnimationUtils.loadAnimation(context, R.anim.rotate_close_anim)}
    private val fromBottom: Animation by lazy {AnimationUtils.loadAnimation(context, R.anim.from_bottom_anim)}
    private val toBottom: Animation by lazy {AnimationUtils.loadAnimation(context, R.anim.to_bottom_anim)}
    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //get trip ID and show it in the UI
        loadAndShowTrip()

        binding.buttonEditTrip.setOnClickListener {
            onEditTripClick()
        }
        binding.buttonChangeDetails.setOnClickListener{
            Toast.makeText(context, "Edit Trip button clicked!", Toast.LENGTH_SHORT).show()
        }
        binding.buttonDeleteTrip.setOnClickListener{
            //delete trip from database and return to main view
            val id = arguments?.getInt(EXTRA_TRIP_ID)
            val loadedTrip = id?.let { getTripFromDB(it) }
            deleteTripFromDB(loadedTrip)
            findNavController().navigate(R.id.action_fromDetailsToMain)
            //Toast.makeText(context, "Delete button clicked!", Toast.LENGTH_SHORT).show()
        }
        return root
    }

    private fun deleteTripFromDB(loadedTrip: TripEntity?) {
        context?.let {
            if (loadedTrip != null) {
                LocalDB.getInstance(it).getTripDAO().deleteTrips(loadedTrip)
            }
        }
    }

    private fun onEditTripClick() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            binding.buttonChangeDetails.startAnimation(fromBottom)
            binding.buttonDeleteTrip.startAnimation(fromBottom)
            binding.buttonEditTrip.startAnimation(rotateOpen)
        } else {
            binding.buttonChangeDetails.startAnimation(toBottom)
            binding.buttonDeleteTrip.startAnimation(toBottom)
            binding.buttonEditTrip.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.buttonChangeDetails.visibility = View.VISIBLE
            binding.buttonDeleteTrip.visibility = View.VISIBLE
        } else {
            binding.buttonChangeDetails.visibility = View.INVISIBLE
            binding.buttonDeleteTrip.visibility = View.INVISIBLE
        }
    }

    /**
     * Button animation
     */
    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.buttonChangeDetails.isClickable = true
            binding.buttonDeleteTrip.isClickable = true
        } else {
            binding.buttonChangeDetails.isClickable = false
            binding.buttonDeleteTrip.isClickable = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadAndShowTrip() {
        // Get trip ID from arguments, load trip details from DB and show it in the UI
        val id = arguments?.getInt(EXTRA_TRIP_ID)

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
            Log.e("TAG", "Failed to create media storage directory!")
        }
        return mediaStorageDir.path + File.separator + fileName + ".jpg"
    }
}