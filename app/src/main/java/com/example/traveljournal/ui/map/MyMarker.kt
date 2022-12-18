package com.example.traveljournal.ui.map

data class MyMarker (
    val title: String,
    val description: String,
    val imageLink: String,
    val pageId: String,
    var lastClicked: Boolean
)