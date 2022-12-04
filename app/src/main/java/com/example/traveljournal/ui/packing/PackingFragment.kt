package com.example.traveljournal.ui.packing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.traveljournal.databinding.FragmentPackingBinding

class PackingFragment : Fragment() {

    private var _binding: FragmentPackingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val documentViewModel =
            ViewModelProvider(this).get(PackingViewModel::class.java)

        _binding = FragmentPackingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textPacking
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
