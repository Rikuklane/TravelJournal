package com.example.traveljournal.ui.packing

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.traveljournal.TravelApplication
import com.example.traveljournal.databinding.FragmentPackingBinding
import com.example.traveljournal.room.packing.PackingItem

class PackingFragment : Fragment(), NewPackingSheet.FinishListener, PackingClickListener {

    private lateinit var binding: FragmentPackingBinding
    private val packingViewModel: PackingViewModel by viewModels {
        PackingViewModelFactory((activity?.application as TravelApplication).repository)
    }

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
        setupRecyclerView()
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
        /* val packingClickListener = PackingAdapter.PackingClickListener { p -> openPackingDetailsFragment(p) }
         packingAdapter = PackingAdapter(model.packingArray, packingClickListener) //Initialize adapter
         binding.recyclerviewPacking.adapter = packingAdapter //Bind recyclerview to adapter
         binding.recyclerviewPacking.layoutManager = LinearLayoutManager(context) //Gives layout */
    }

    /**
     * Activity change: opens packing details view
     */
    private fun openPackingDetailsFragment(packing: PackingItem) {
        val bundle = Bundle()
        //  bundle.putLong(PackingDetailsFragment.EXTRA_TRIP_ID, packing.id)
        //  findNavController().navigate(R.id.action_openPackingDetailsFragment, bundle)
    }

    /**
     * Activity change: allows the user to enter a new packing to the database
     */
    private fun openNewPackingFragment() {
        //  findNavController().navigate(R.id.action_openNewPackingFragment)
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
