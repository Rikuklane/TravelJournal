package com.example.traveljournal.ui.documents

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentEditDocumentBinding
import com.example.traveljournal.databinding.FragmentNewTripBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.TripEntity
import com.example.traveljournal.ui.documents.EditDocumentFragment.Companion.EXTRA_DOCUMENT_ID
import com.example.traveljournal.ui.trips.TripDetailsFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditDocumentFragment : Fragment() {

    private val TAG = "EditDocumentFragment"
    private var _binding: FragmentEditDocumentBinding? = null
    companion object { const val EXTRA_DOCUMENT_ID = "docId" }

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDocumentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val id = arguments?.getLong(EXTRA_DOCUMENT_ID)

        editDocument(id)
        setupSaveButton(id)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editDocument(id: Long?) {
        Log.i("EditDocument", id.toString())
        if (id != null) {

            val docs = LocalDB.getDocumentsInstance(requireContext()).getDocumentDAO().loadDocuments()
            var doc = docs.find { t -> id == t.id}!!

            binding.editDocType.hint = doc.type
            //binding.editDocExpDate.hint = doc.expiration as Editable
            Log.i("EditDocument", doc.type.toString())
        }
    }

    private fun setupSaveButton(id: Long?) {
        binding.saveEditedDoc.setOnClickListener {
            Log.i("EditDocument", id.toString())
            // Fetch the values from UI user input
            val editTexts = listOf(
                binding.editDocType,
                binding.editDocExpDate
            )

            val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
            val parsedExpDate = parseDate(editTexts[1].text.toString())
            if (!allEditTextsHaveContent or (parsedExpDate == null)) {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show() // Input is not valid
            } else {
                // Change data in DB
                if (id != null) {
                    val docs = LocalDB.getDocumentsInstance(requireContext()).getDocumentDAO().loadDocuments()
                    var doc = docs.find { t -> id == t.id}!!

                    doc.type = editTexts[0].text.toString()
                    doc.expiration = parsedExpDate
                    context?.let { LocalDB.getDocumentsInstance(it).getDocumentDAO().insertDocument(doc) }
                    findNavController().navigate(R.id.action_backToDocs)
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


}