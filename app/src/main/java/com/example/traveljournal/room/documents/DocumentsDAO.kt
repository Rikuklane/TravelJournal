package com.example.traveljournal.room.documents

import androidx.room.*

@Dao
interface DocumentsDAO {
    @Query("SELECT * FROM documents")
    fun loadDocuments(): Array<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDocument(vararg documents: DocumentEntity)

    @Delete
    fun deleteDocument(vararg documents: DocumentEntity)
}