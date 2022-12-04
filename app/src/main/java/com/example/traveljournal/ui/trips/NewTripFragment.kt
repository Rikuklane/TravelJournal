package com.example.traveljournal.ui.trips

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.traveljournal.databinding.FragmentNewTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import java.io.File

class NewTripFragment : Fragment() {

    private var _binding: FragmentNewTripBinding? = null

    //Handles camera results
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photoID = binding.enterCountryEditText.text.toString()
            showPicture(getPhotoFile(photoID).path)
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewTripBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSaveButton()

        //createdTimeEditText needed for image filename
        binding.buttonTakePhoto.setOnClickListener {
            if (binding.enterCountryEditText.text.isNotEmpty()){
                openCameraButton()
            } else {
                Toast.makeText(context, "Please enter country name first", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            // Fetch the values from UI user input
            val newTrip = getUserEnteredTrip()
            // Store them in DB
            if (newTrip != null){
                saveTripToDB(newTrip)
            } else {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //TODO: add another button which would allow the user to upload pictures from the gallery

    private fun openCameraButton() {
        //Base from project guide https://courses.cs.ut.ee/2022/MAD/fall/Main/MiniProject1

        //TODO: Create a better unique ID for picture filename
        val uniqueID = binding.enterCountryEditText.text.toString()
        val photoFile = getPhotoFile(uniqueID)
        val fileUri = context?.let { FileProvider.getUriForFile(it, "com.example.traveljournal.fileprovider", photoFile) }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        launcher.launch(takePictureIntent)
    }

    //Creating Image
    private fun showPicture(fileName: String) {
        val bmp = BitmapFactory.decodeFile(fileName)
        binding.tripImageView.setImageBitmap(bmp)
    }

    //Base from project guide https://courses.cs.ut.ee/2022/MAD/fall/Main/MiniProject1
    private fun getPhotoFile(fileName: String): File {
        val mediaStorageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("TAG", "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName + ".jpg")
    }

    private fun saveTripToDB(newTrip: TripEntity) {
        context?.let { LocalDB.getInstance(it).getTripDAO().insertTrips(newTrip) }
    }

    private fun getUserEnteredTrip(): TripEntity? {
        val editTexts = listOf(
            binding.enterCountryEditText,
            binding.enterDatesEditText,
            binding.tripSummaryEditText
        )

        val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }

        //if some textfields are empty, return null
        if (!allEditTextsHaveContent) {
            return null
        }

        //create a new trip entity based on user entered values
        return TripEntity(
            0,
            binding.enterCountryEditText.text.toString(),
            binding.enterDatesEditText.text.toString(),
            binding.tripSummaryEditText.text.toString()
        )
    }
}