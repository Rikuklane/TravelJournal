package com.example.traveljournal.ui.trips

import android.graphics.BitmapFactory
import android.os.Bundle
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

class TripDetailsFragment : Fragment() {


    private lateinit var trip : TripEntity
    private var _binding: FragmentTripDetailsBinding? = null
    companion object { const val EXTRA_TRIP_ID = "tripId" }

    // This property is only valid between onCreateView and onDestroyView.
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

        // setup buttons
        setupOpenOptionsButton()
        setupEditButton()
        setupDeleteButton()

        return root
    }

    private fun deleteTripFromDB() {
        LocalDB.getTripsInstance(requireContext()).getTripDAO().deleteTrips(trip)
    }

    private fun setupEditButton() {
        binding.buttonEditTrip.setOnClickListener {
            //Toast.makeText(context, "Edit Trip button clicked!", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putLong(EXTRA_TRIP_ID, trip.id)
            findNavController().navigate(R.id.action_editTrip, bundle)
        }
    }

    private fun setupOpenOptionsButton() {
        binding.buttonOpenOptions.setOnClickListener {
            setVisibility(clicked)
            setAnimation(clicked)
            setClickable(clicked)
            clicked = !clicked
        }
    }

    private fun setupDeleteButton() {
        binding.buttonDeleteTrip.setOnClickListener{
            //delete trip from database and return to main view
            deleteTripFromDB()
            findNavController().navigate(R.id.action_fromDetailsToMain)
        }
    }

    // TODO maybe should create a view model for this and put all the Live changes into the view model?
    //  - the idea is to have data like in packingList viewModel
    //  - the same goes for other similar functions
    private fun setAnimation(clicked: Boolean) {
        if(!clicked){
            binding.buttonEditTrip.startAnimation(fromBottom)
            binding.buttonDeleteTrip.startAnimation(fromBottom)
            binding.buttonOpenOptions.startAnimation(rotateOpen)
        } else {
            binding.buttonEditTrip.startAnimation(toBottom)
            binding.buttonDeleteTrip.startAnimation(toBottom)
            binding.buttonOpenOptions.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.buttonEditTrip.visibility = View.VISIBLE
            binding.buttonDeleteTrip.visibility = View.VISIBLE
        } else {
            binding.buttonEditTrip.visibility = View.INVISIBLE
            binding.buttonDeleteTrip.visibility = View.INVISIBLE
        }
    }

    /**
     * Button animation
     */
    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.buttonEditTrip.isClickable = true
            binding.buttonDeleteTrip.isClickable = true
        } else {
            binding.buttonEditTrip.isClickable = false
            binding.buttonDeleteTrip.isClickable = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadAndShowTrip() {
        // Get trip ID from arguments, load trip details from DB and show it in the UI
        val id = arguments?.getLong(EXTRA_TRIP_ID)

        if (id != null) {
            getTripFromDB(id)
            showTrip()
        }
    }

    private fun getTripFromDB(id: Long){
        val trips = LocalDB.getTripsInstance(requireContext()).getTripDAO().loadTrips()
        trip = trips.find { t -> id == t.id}!!
    }

    private fun showTrip() {

        trip.apply {
            //TODO: create an xml file and assign values to textviews etc
            binding.detailsCountryTextView.text = this.country
            binding.detailsTripSummary.text = this.summary
            binding.detailsTripDates.text = this.dateFrom.toString()
            binding.detailsTripDates.text = this.dateTo.toString()  // TODO add field to xml to hold 2 dates
            if (this.image != "") {
                binding.detailsImageView.setImageBitmap(BitmapFactory.decodeFile(this.image))
            }
        }
    }
}
