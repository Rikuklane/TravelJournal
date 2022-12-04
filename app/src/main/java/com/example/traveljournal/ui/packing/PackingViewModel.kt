package com.example.traveljournal.ui.packing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PackingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is packing Fragment"
    }
    val text: LiveData<String> = _text
}
