package com.example.traveljournal.ui.documents

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentDocumentsBinding
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.documents.DocumentEntity
import com.example.traveljournal.ui.trips.TripDetailsFragment

class DocumentFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private lateinit var documentsAdapter: DocumentsAdapter
    private val model: DocumentViewModel by viewModels()
    private val TAG = "DocumentsFragment"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val documentViewModel =
            ViewModelProvider(this).get(DocumentViewModel::class.java)

        _binding = FragmentDocumentsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        binding.buttonNewDocument.setOnClickListener{ openNewDocumentFragment() }
        return root
    }

    private fun openNewDocumentFragment() {
        findNavController().navigate(R.id.action_nav_newDocument)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        model.refresh(documentsAdapter)
        documentsAdapter.data = model.documentsArray
        documentsAdapter.notifyDataSetChanged()
    }

    /**
     * sets up a recycler view for  trips
     */
    private fun setupRecyclerView() {
        val documentClickListener = DocumentsAdapter.DocumentClickListener { p -> documentOptions(p) }
        documentsAdapter = DocumentsAdapter(model.documentsArray, documentClickListener) //Initialize adapter
        binding.recyclerviewDocuments.adapter = documentsAdapter //Bind recyclerview to adapter
        binding.recyclerviewDocuments.layoutManager = LinearLayoutManager(context) //Gives layout
    }

    fun documentOptions(doc: DocumentEntity) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.doc_dialog)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        val deleteBtn: Button = dialog.findViewById(R.id.deleteDocBtn)
        val editBtn: Button = dialog.findViewById((R.id.editDocBtn))

        deleteBtn.setOnClickListener() {
            LocalDB.getDocumentsInstance(requireContext()).getDocumentDAO().deleteDocument(doc)
            model.refresh(documentsAdapter)
            documentsAdapter.data = model.documentsArray
            documentsAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        editBtn.setOnClickListener() {
            val bundle = Bundle()
            bundle.putLong(EditDocumentFragment.EXTRA_DOCUMENT_ID, doc.id)
            findNavController().navigate(R.id.action_nav_editDocument, bundle)
            dialog.dismiss()
        }
        dialog.show()
    }
}
