package com.example.traveljournal.ui.documents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traveljournal.databinding.FragmentDocumentBinding

class DocumentFragment : Fragment() {

    private var _binding: FragmentDocumentBinding? = null

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

        _binding = FragmentDocumentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDocument
        documentViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
