package com.example.traveljournal.ui.documents

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentNewDocumentBinding
import com.example.traveljournal.databinding.FragmentNewTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.documents.DocumentEntity
import com.example.traveljournal.room.trips.TripEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewDocumentFragment : Fragment() {
    private val TAG = "NewDocumentFragment"
    private var _binding: FragmentNewDocumentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewDocumentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSaveButton()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            // Fetch the values from UI user input
            val newDocument = getUserEnteredDocument()
            // Store them in DB
            if (newDocument != null){
                //after saving the new trip, the view navigates back to all Trips
                saveDocumentToDB(newDocument)
                findNavController().navigate(R.id.action_backToAllDocs)
            } else {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDocumentToDB(newDocument: DocumentEntity) {
        context?.let {LocalDB.getDocumentsInstance(it).getDocumentDAO().insertDocument(newDocument)}
    }

    private fun getUserEnteredDocument(): DocumentEntity? {
        val editTexts = listOf(
            binding.editIdType,
            binding.editExpiryDate
        )

        val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
        val parsedDate = parseDate(editTexts[1].text.toString())
        if (!allEditTextsHaveContent or (parsedDate == null)) {
            return null // Input is not valid
        }
        //create a new trip entity based on user entered values
        val document = DocumentEntity(
            0,
            editTexts[0].text.toString(),
            parsedDate
        )
        Log.i(
            TAG,
            " ${document.id} + ${document.type} + ${document.expiration}"
        )
        return document
    }

    /** Tries to parse argument string as a Date in format specified by TripEntity.DATEFORMAT,
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
}