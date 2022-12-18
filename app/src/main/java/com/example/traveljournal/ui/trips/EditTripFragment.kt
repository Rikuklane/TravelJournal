package com.example.traveljournal.ui.trips

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentEditDocumentBinding
import com.example.traveljournal.databinding.FragmentEditTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditTripFragment : Fragment() {
    private val TAG = "EditTripFragment"
    private var _binding: FragmentEditTripBinding? = null
    companion object { const val EXTRA_TRIP_ID = "tripId" }
    private lateinit var imageUri: Uri
    private var imagePath: String = ""

    private var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Image saved: $it")
                val bitmap = loadImage(imageUri)
                binding.editTripImageView.setImageBitmap(bitmap)
            } else {
                Log.i(TAG, "Failed creating image")
            }
        }

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTripBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id = arguments?.getLong(EXTRA_TRIP_ID)

        editTrip(id)
        setupCameraButton()
        setupSaveButton(id)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editTrip(id: Long?) {
        Log.i("EditTrip", id.toString())
        if (id != null) {

            val trips = LocalDB.getTripsInstance(requireContext()).getTripDAO().loadTrips()
            var trip = trips.find { t -> id == t.id}!!
            //TODO make the default values of the widgets that of the original trip


        }
    }

    private fun setupSaveButton(id: Long?) {
        binding.editTripSaveBtn.setOnClickListener {
            Log.i("EditTrip", id.toString())
            // Fetch the values from UI user input
            val editTexts = listOf(
                binding.editTripCountryName,
                binding.editTripSummary,
                binding.editTripFromDate,
                binding.editTripToDate,
            )

            val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
            val parsedFromDate = parseDate(editTexts[2].text.toString())
            val parsedToDate = parseDate(editTexts[3].text.toString())
            if (!allEditTextsHaveContent or (parsedFromDate == null) or (parsedToDate == null)) {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show() // Input is not valid
            } else {
                // Change data in DB
                if (id != null) {
                    val trips = LocalDB.getTripsInstance(requireContext()).getTripDAO().loadTrips()
                    var trip = trips.find { t -> id == t.id}!!

                    trip.country = editTexts[0].text.toString()
                    trip.dateFrom = parsedFromDate
                    trip.dateTo = parsedToDate
                    trip.summary = editTexts[1].text.toString()
                    trip.image = imagePath

                    context?.let { LocalDB.getTripsInstance(it).getTripDAO().insertTrips(trip) }
                    findNavController().navigate(R.id.action_allTrips)
                }
            }
        }
    }

    /** Tries to parse argument string as a Date in format specified by DocumentEntity.DATEFORMAT,
     * returns null if fails or a Date object if succeeded */
    private fun parseDate(inDate: String): Date? {
        val dateFormat = SimpleDateFormat(TripEntity.DATEFORMAT, Locale.getDefault())
        dateFormat.isLenient = false
        var parsedDate: Date? = null
        try {
            parsedDate = dateFormat.parse(inDate)
        } catch (pe: ParseException) {
            return parsedDate
        }
        return parsedDate
    }

    private fun getPhotoFile(fileName: String): File {
        // Get safe storage directory for photos. Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }
        imagePath = mediaStorageDir.path + File.separator + fileName
        return File(imagePath)
    }

    private fun loadImage(uri: Uri): Bitmap {
        activity?.application?.contentResolver?.openInputStream(uri).use {
            val fullBitmap = BitmapFactory.decodeStream(it)
            val ratio = fullBitmap.width.toDouble() / fullBitmap.height
            return Bitmap.createScaledBitmap(fullBitmap, (1000 * ratio).toInt(), 1000, false)
        }
    }

    private fun checkCameraPermission() = ActivityCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() =
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            binding.editTripTakePhotoBtn.performClick()
        } else {
            Toast.makeText(context, "Can not open camera without permissions!", Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Can't take pictures without camera permissions!")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupCameraButton() {
        //Base from project guide https://courses.cs.ut.ee/2022/MAD/fall/Main/MiniProject1
        binding.editTripTakePhotoBtn.setOnClickListener {
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

}