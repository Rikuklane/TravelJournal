package com.example.traveljournal.ui.trips

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentNewTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.DateTypeConverter
import com.example.traveljournal.room.trips.TripEntity
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NewTripFragment : Fragment() {

    private val TAG = "NewTripFragment"
    private var _binding: FragmentNewTripBinding? = null
    private lateinit var tripsGalleryAdapter: TripGalleryAdapter
    private lateinit var imageUri: Uri
    private val converter = DateTypeConverter()
    private val formatter = SimpleDateFormat("dd.MMM yyyy", Locale.UK)
    private var imagePathList: List<String> = mutableListOf()
    private var fromDate: Date = Date()
    private var toDate: Date = Date()
    private var trip: TripEntity? = null
    companion object { const val ID = "id"
    }

    @SuppressLint("NotifyDataSetChanged")
    private var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Image saved: $it")
                tripsGalleryAdapter.data = imagePathList
                tripsGalleryAdapter.notifyDataSetChanged()
            } else {
                Log.i(TAG, "Failed creating image")
            }
        }

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            getTripFromDB(it.getLong(ID))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTripBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupSaveButton()
        setupCameraButton()
        setupDateRangePicker()
        setupRecyclerView()
        if (trip != null) {
            setUpEditView()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        tripsGalleryAdapter = TripGalleryAdapter(imagePathList) //Initialize adapter
        binding.recyclerView.adapter = tripsGalleryAdapter //Bind recyclerview to adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false) //Gives layout
    }

    private fun setupDateRangePicker() {
        val calendarButton = binding.selectDateButton
        val materialDateBuilder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()

        materialDateBuilder.setTitleText(R.string.hint_select_date)

        val materialDatePicker: MaterialDatePicker<Pair<Long, Long>> = materialDateBuilder.build()

        calendarButton.setOnClickListener {
            activity?.let { it1 -> materialDatePicker.show(it1.supportFragmentManager, "MATERIAL_DATE_PICKER") }
        }

        materialDatePicker.addOnPositiveButtonClickListener {
            fromDate = converter.toDate(it.first)
            toDate = converter.toDate(it.second)

            calendarButton.text = getString(
                R.string.selectedDate, formatter.format(fromDate), formatter.format(toDate)
            )
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            // Fetch the values from UI user input
            val newTrip = getUserEnteredTrip()
            // Store them in DB
            if (newTrip != null) {
                if (trip != null){newTrip.id = trip!!.id}
                //after saving the new trip, the view navigates back to all Trips
                saveTripToDB(newTrip)
                findNavController().navigate(R.id.action_backToAllTrips)
            } else {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setUpEditView(){
        binding.newTripTitleText.text = getString(R.string.edit_trip)
        binding.enterCountryEditText.setText(trip!!.country)
        binding.tripSummaryEditText.setText(trip!!.summary)
        binding.tripPackingList.setText(trip!!.packingList)
        fromDate = trip!!.dateFrom!!
        toDate = trip!!.dateTo!!

        binding.selectDateButton.text = getString(
            R.string.selectedDate, formatter.format(fromDate), formatter.format(toDate)
        )
        if (!trip!!.images.equals("")) {
            imagePathList = trip!!.images?.split(",")!!
            tripsGalleryAdapter.data = imagePathList
        }
        tripsGalleryAdapter.notifyDataSetChanged()
    }

    //TODO: add another button which would allow the user to upload pictures from the gallery

    private fun checkCameraPermission() = ActivityCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() =
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.buttonOpenCamera.performClick()
        } else {
            Toast.makeText(context, "Can not open camera without permissions!", Toast.LENGTH_SHORT)
                .show()
            Log.w(TAG, "Can't take pictures without camera permissions!")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupCameraButton() {
        //Base from project guide https://courses.cs.ut.ee/2022/MAD/fall/Main/MiniProject1
        binding.buttonOpenCamera.setOnClickListener {
            if (!checkCameraPermission()) {
                requestCameraPermission()
            } else {
                val timestamp = SimpleDateFormat("ddMMyy_mmHHssSSS").format(Date())
                val photoFile = getPhotoFile("$timestamp.jpg")
                imageUri = context?.let { it1 ->
                    FileProvider.getUriForFile(
                        it1, "com.example.traveljournal.fileprovider", photoFile
                    )
                }!!

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                launcher.launch(takePictureIntent)
            }
        }
    }

    /**
     * -- Base from project guide https://courses.cs.ut.ee/2022/MAD/fall/Main/MiniProject1 --
     * Returns the File for a photo stored on disk given the fileName.
     * Creating the storage directory if it does not exist:
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun getPhotoFile(fileName: String): File {
        // Get safe storage directory for photos. Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }
        val imagePath = mediaStorageDir.path + File.separator + fileName
        imagePathList = imagePathList.plus(imagePath)
        return File(imagePath)
    }

    private fun saveTripToDB(newTrip: TripEntity) {
        trip?.let {LocalDB.getTripsInstance(requireContext()).getTripDAO().deleteTrips(trip!!)  }
        context?.let { LocalDB.getTripsInstance(it).getTripDAO().insertTrips(newTrip) }
    }

    private fun getTripFromDB(id: Long){
        val trips = LocalDB.getTripsInstance(requireContext()).getTripDAO().loadTrips()
        trip = trips.find { t -> id == t.id}!!
    }

    private fun getUserEnteredTrip(): TripEntity? {
        val editTexts = listOf(
            binding.enterCountryEditText,
            binding.tripSummaryEditText,
            binding.tripPackingList
        )

        val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
        if (!allEditTextsHaveContent or binding.selectDateButton.text.equals(R.string.hint_select_date)) {
            return null // Input is not valid
        }

        //create a new trip entity based on user entered values
        val trip = TripEntity(
            0,
            editTexts[0].text.toString(),
            editTexts[1].text.toString(),
            fromDate,
            toDate,
            imagePathList.joinToString(),
            editTexts[4].text.toString(),
        )
        Log.i(
            TAG,
            " ${trip.country} + ${trip.summary} + ${trip.dateFrom} + ${trip.dateTo}"
        )
        return trip
    }
}

