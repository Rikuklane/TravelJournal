package com.example.traveljournal.ui.packing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.traveljournal.databinding.FragmentNewPackingBinding
import com.example.traveljournal.databinding.FragmentPackingBinding
import com.example.traveljournal.room.packing.PackingItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NewPackingSheet(private val listener: FinishListener) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNewPackingBinding
    //private lateinit var packingViewModel: PackingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()

        //packingViewModel = ViewModelProvider(activity)[packingViewModel::class.java]
        binding.saveButton.setOnClickListener {
            saveAction()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewPackingBinding.inflate(inflater, container, false)
        return binding.root
    }


    private fun saveAction() {
        val content = binding.name.text.toString()
        listener.onSaveClicked(content)
        dismiss()
    }

    interface FinishListener {
        fun onSaveClicked(content: String)
    }

}