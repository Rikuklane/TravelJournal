package com.example.traveljournal.ui.documents

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentEditDocumentBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.trips.DateTypeConverter
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class EditDocumentFragment : Fragment() {

    private var _binding: FragmentEditDocumentBinding? = null
    companion object { const val EXTRA_DOCUMENT_ID = "docId" }
    private lateinit var expiredDate: Date
    private val converter = DateTypeConverter()
    private val formatter = SimpleDateFormat("dd.MMM yyyy", Locale.UK)

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
        setupDateRangePicker()

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
            val doc = docs.find { t -> id == t.id}!!

            binding.editDocType.setText(doc.type)
            expiredDate = doc.expiration!!
            binding.expiryDateButton.text = getString(
                R.string.expiry_date, formatter.format(expiredDate)
            )
            Log.i("EditDocument", doc.type.toString())
        }
    }

    private fun setupSaveButton(id: Long?) {
        binding.saveEditedDoc.setOnClickListener {
            Log.i("EditDocument", id.toString())
            // Fetch the values from UI user input
            val editTexts = listOf(
                binding.editDocType,
            )

            val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }
            if (!allEditTextsHaveContent) {
                Toast.makeText(context, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show() // Input is not valid
            } else {
                // Change data in DB
                if (id != null) {
                    val docs = LocalDB.getDocumentsInstance(requireContext()).getDocumentDAO().loadDocuments()
                    val doc = docs.find { t -> id == t.id}!!

                    doc.type = editTexts[0].text.toString()
                    doc.expiration = expiredDate
                    context?.let { LocalDB.getDocumentsInstance(it).getDocumentDAO().insertDocument(doc) }
                    findNavController().navigate(R.id.action_backToDocs)
                }
            }
        }
    }

    private fun setupDateRangePicker() {
        val calendarButton = binding.expiryDateButton
        val materialDateBuilder: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()

        materialDateBuilder.setTitleText(R.string.hint_select_date)

        val materialDatePicker: MaterialDatePicker<Long> = materialDateBuilder.build()

        calendarButton.setOnClickListener {
            activity?.let { it1 -> materialDatePicker.show(it1.supportFragmentManager, "MATERIAL_DATE_PICKER") }
        }

        materialDatePicker.addOnPositiveButtonClickListener {
            expiredDate = converter.toDate(it)
            calendarButton.text = getString(R.string.expiry_date, formatter.format(expiredDate))
        }
    }
}
