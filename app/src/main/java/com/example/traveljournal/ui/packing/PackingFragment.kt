package com.example.traveljournal.ui.packing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.TravelApplication
import com.example.traveljournal.databinding.FragmentPackingBinding
import com.example.traveljournal.room.packing.PackingItem
import java.util.*

class PackingFragment : Fragment(), NewPackingSheet.FinishListener, PackingClickListener {

    private lateinit var binding: FragmentPackingBinding
    private val packingViewModel: PackingViewModel by viewModels {
        PackingViewModelFactory((activity?.application as TravelApplication).repository)
    }
    private val TAG = "PackingFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionsAndSetupRecyclerView()
    }

    override fun onResume() {
        super.onResume()

        binding.saveButton.setOnClickListener { openNewPackingFragment() }
        binding.saveButton.setOnClickListener {
            NewPackingSheet(this).show(requireActivity().supportFragmentManager, "newPackingTag")
        }
    }

    /**
     * sets up a recycler view for  packing
     */
    private fun setupRecyclerView() {
        packingViewModel.packingItems.observe(this){
            binding.todoListRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireActivity().applicationContext)
                adapter = PackingItemAdapter(it, this@PackingFragment)
            }
        }
    }

    /**
     * Activity change: opens packing details view
     */
    private fun openPackingDetailsFragment(packing: PackingItem) {
        val bundle = Bundle()
    }

    /**
     * Activity change: allows the user to enter a new packing to the database
     */
    private fun openNewPackingFragment() {
        //  findNavController().navigate(R.id.action_openNewPackingFragment)
    }

    private fun checkPermissionsAndSetupRecyclerView() {
        if (checkStoragePermission()) {
            setupRecyclerView()
        } else {
            requestStoragePermission()
        }
    }

    private fun checkStoragePermission() =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED)
        }

    private fun requestStoragePermission() =
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "Storage permissions granted")
            setupRecyclerView()
        } else {
            Toast.makeText(
                context,
                "Can't load the images without external storage permissions!",
                Toast.LENGTH_SHORT
            ).show()
            Log.w(TAG, "Can't see the packing without external storage permissions!")
        }
    }

    override fun onSaveClicked(content: String) {
        val packingItem = PackingItem(0, content)
        packingViewModel.addPackingItem(packingItem)
    }

    override fun completeTaskItem(packingItem: PackingItem) {
        Handler(Looper.getMainLooper()).postDelayed(
            {
                packingViewModel.deletePackingItem(packingItem) // This method will be executed once the timer is over
            },
            500 // value in milliseconds
        )
    }
}
