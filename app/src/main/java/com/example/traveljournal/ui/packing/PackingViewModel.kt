package com.example.traveljournal.ui.packing

import androidx.lifecycle.*
import com.example.traveljournal.room.packing.PackingItem
import com.example.traveljournal.room.packing.PackingItemRepository
import kotlinx.coroutines.launch


class PackingViewModel(private val repository: PackingItemRepository): ViewModel() {
    var packingItems: LiveData<List<PackingItem>> = repository.allPackingItems.asLiveData()

    fun addPackingItem(packingItem: PackingItem) = viewModelScope.launch{
        repository.insertPackingItem(packingItem)
    }
    fun deletePackingItem(packingItem: PackingItem) = viewModelScope.launch {
        repository.deletePackingItem(packingItem)
    }

}
class PackingViewModelFactory(private val repository: PackingItemRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(PackingViewModel::class.java))
            return PackingViewModel(repository) as T

        throw IllegalArgumentException("Siin ei peaks olema")
    }
}