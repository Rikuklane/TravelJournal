package com.example.traveljournal.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.R
import com.example.traveljournal.databinding.FragmentDocumentsBinding

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
        documentsAdapter = DocumentsAdapter(model.documentsArray) //Initialize adapter
        binding.recyclerviewDocuments.adapter = documentsAdapter //Bind recyclerview to adapter
        binding.recyclerviewDocuments.layoutManager = LinearLayoutManager(context) //Gives layout
    }
}
