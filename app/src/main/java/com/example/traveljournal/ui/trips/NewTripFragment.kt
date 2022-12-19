package com.example.traveljournal.ui.trips

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentNewTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class NewTripFragment : Fragment(), DateSelected {

    private val TAG = "NewTripFragment"
    private var _binding: FragmentNewTripBinding? = null
    private lateinit var tripsAdapter: TripGalleryAdapter
    private lateinit var imageUri: Uri
    private var imagePathList: List<String> = mutableListOf()
    private var fromDate: Date? = Date()
    private var toDate: Date? = Date()
    private var trip: TripEntity? = null

    companion object {
        const val ID = "id"
    }

    @SuppressLint("NotifyDataSetChanged")
    private var launcher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Image saved: $it")
                tripsAdapter.data = imagePathList
                tripsAdapter.notifyDataSetChanged()
            } else {
                Log.i(TAG, "Failed creating image")
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val pickedPhoto = it.data?.data
                //TODO: uri to path
                //imagePathList = imagePathList.plus(pickedPhoto?.let { it1 -> getRealPathFromURI(it1)}.toString())
                //Log.i(TAG, imagePathList.toString())
                //tripsAdapter.data = imagePathList
                //tripsAdapter.notifyDataSetChanged()
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
        setUpGalleryOpen()
        setupCameraButton()
        // TODO finish setupDateRangePicker()
        setUpDatePickers()
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
        tripsAdapter = TripGalleryAdapter(imagePathList) //Initialize adapter
        binding.recyclerView.adapter = tripsAdapter //Bind recyclerview to adapter
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) //Gives layout
    }

    private fun setupDateRangePicker() {
        val calendarButton = binding.editFromDate
        val materialDateBuilder: MaterialDatePicker.Builder<Pair<Long, Long>> =
            MaterialDatePicker.Builder.dateRangePicker()


        materialDateBuilder.setTitleText("SELECT A DATE")

        val materialDatePicker: MaterialDatePicker<Pair<Long, Long>> = materialDateBuilder.build()

        calendarButton.setOnClickListener {
            activity?.let { it1 -> materialDatePicker.show(it1.supportFragmentManager, "MATERIAL_DATE_PICKER") }
        }

        materialDatePicker.addOnPositiveButtonClickListener {
            calendarButton.text = materialDatePicker.headerText

            val timeZoneUTC = TimeZone.getDefault()
            // It will be negative, so that's the -1
            val offsetFromUTC = timeZoneUTC.getOffset(Date().time) * -1

            // TODO create a normal format with calendar as with the previous version.
            // Create a date format, then a date object with our offset
            val simpleFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val date1 = Date(it.first + offsetFromUTC)
            val date2 = Date(it.second + offsetFromUTC)

            // TODO set date to text
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            // Fetch the values from UI user input
            val newTrip = getUserEnteredTrip()
            // Store them in DB
            if (newTrip != null) {
                if (trip != null) {
                    newTrip.id = trip!!.id
                }
                //after saving the new trip, the view navigates back to all Trips
                saveTripToDB(newTrip)
                findNavController().navigate(R.id.action_backToAllTrips)
            } else {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setUpDatePickers() {
        val formatter = DateTimeFormatter.ofPattern("dd.MMM yyyy")
        binding.editFromDate.text = LocalDate.now().format(formatter)
        binding.editFromDate.setOnClickListener { showDatePicker(true) }

        binding.editToDate.text = LocalDate.now().format(formatter)
        binding.editToDate.setOnClickListener { showDatePicker(false) }
    }

    private fun showDatePicker(begin: Boolean) {
        val datePickerFragment = DatePickerFragment(this, begin)
        datePickerFragment.show(requireFragmentManager(), "DatePicker")
    }

    private fun setUpEditView() {
        binding.newTripTitleText.text = getString(R.string.edit_trip)
        binding.enterCountryEditText.setText(trip!!.country)
        binding.tripSummaryEditText.setText(trip!!.summary)
        binding.tripPackingList.setText(trip!!.packingList)
        trip!!.dateFrom?.let {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = it
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            receiveDate(year, month, day, true)
        }
        trip!!.dateTo?.let {
            val calendar: Calendar = Calendar.getInstance()
            calendar.time = it
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            receiveDate(year, month, day, false)
        }
        if (!trip!!.images.equals("")) {
            imagePathList = trip!!.images?.split(",")!!
            tripsAdapter.data = imagePathList
        }
    }


    private fun checkCameraPermission() = ActivityCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkGalleryPermission() = ActivityCompat.checkSelfPermission(
        requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() =
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    private fun requestGalleryPermission() =
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

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

    private fun setUpGalleryOpen() {
        binding.buttonOpenGallery.setOnClickListener {
            if (!checkGalleryPermission()) {
                requestGalleryPermission()
            } else {
                val i = Intent()
                i.type = "image/*"
                i.action = Intent.ACTION_GET_CONTENT
                galleryLauncher.launch(i)
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = context?.let { CursorLoader(it, contentUri, proj, null, null, null) }
        val cursor: Cursor? = loader?.loadInBackground()
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = column_index?.let { cursor.getString(it) }
        cursor?.close()
        return result
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
        trip?.let { LocalDB.getTripsInstance(requireContext()).getTripDAO().deleteTrips(trip!!) }
        context?.let { LocalDB.getTripsInstance(it).getTripDAO().insertTrips(newTrip) }
    }

    private fun getTripFromDB(id: Long) {
        val trips = LocalDB.getTripsInstance(requireContext()).getTripDAO().loadTrips()
        trip = trips.find { t -> id == t.id }!!
    }

    private fun getUserEnteredTrip(): TripEntity? {
        val editTexts = listOf(
            binding.enterCountryEditText,
            binding.tripSummaryEditText,
            binding.editFromDate,
            binding.editToDate,
            binding.tripPackingList
        )

        val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
        val parsedFromDate = fromDate
        val parsedToDate = toDate
        imagePathList
        if (!allEditTextsHaveContent or (parsedFromDate == null) or (parsedToDate == null)) {
            return null // Input is not valid
        }

        //create a new trip entity based on user entered values
        val trip = TripEntity(
            0,
            editTexts[0].text.toString(),
            editTexts[1].text.toString(),
            parsedFromDate,
            parsedToDate,
            imagePathList.joinToString(),
            editTexts[4].text.toString(),
        )
        Log.i(
            TAG,
            " ${trip.country} + ${trip.summary} + ${trip.dateFrom} + ${trip.dateTo}"
        )
        return trip
    }

    class DatePickerFragment(private val dateSelected: DateSelected, val begin: Boolean) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(this.requireContext(), this, year, month, day)
        }

        override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
            dateSelected.receiveDate(year, month, day, begin)
            Log.i("DATEPICKER", "date picker")
        }
    }

    /**
     * Changes button texts when date of beginning or end of the trip is changed
     */

    override fun receiveDate(year: Int, month: Int, day: Int, begin: Boolean) {
        val calendar = GregorianCalendar()
        calendar.set(year, month, day)
        val formatter = SimpleDateFormat("dd.MMM yyyy")
        val formattedDate = formatter.format(calendar.time)
        if (begin) {
            binding.editFromDate.text = formattedDate
            fromDate = calendar.time
        } else {
            binding.editToDate.text = formattedDate
            toDate = calendar.time
        }

    }
}

interface DateSelected {
    fun receiveDate(year: Int, month: Int, day: Int, begin: Boolean)
}

