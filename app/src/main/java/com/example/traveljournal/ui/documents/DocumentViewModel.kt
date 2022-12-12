package com.example.traveljournal.ui.documents

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.traveljournal.room.LocalDB
import com.example.traveljournal.room.documents.DocumentEntity


class DocumentViewModel(val app: Application) : AndroidViewModel(app) {
    private val TAG = "DocumentViewModel"
    var documentsArray: Array<DocumentEntity> = arrayOf()


    fun refresh(documentsAdapter: DocumentsAdapter) {
        val db = LocalDB.getDocumentsInstance(app)
        try {
            documentsArray = db.getDocumentDAO().loadDocuments()
            documentsAdapter.notifyDataSetChanged()
            Log.i(TAG, "Documents refreshed")
        } catch (exception: Exception){
            Log.i(TAG, "Error refreshing documents: $exception")
        }
    }
}
