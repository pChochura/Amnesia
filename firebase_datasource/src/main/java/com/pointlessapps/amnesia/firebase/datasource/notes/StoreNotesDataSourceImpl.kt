package com.pointlessapps.amnesia.firebase.datasource.notes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.amnesia.datasource.notes.StoreNotesDataSource
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto
import kotlinx.coroutines.tasks.await

internal class StoreNotesDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : StoreNotesDataSource {

    private fun getNotesCollection() = firestore
        .collection(USERS_COLLECTION)
        .document(
            requireNotNull(auth.currentUser).uid,
        )
        .collection(NOTES_COLLECTION)

    override suspend fun getAllNotes(): List<NoteDto> =
        getNotesCollection().get().await().toObjects(NoteDto::class.java)

    override suspend fun saveNote(note: NoteDto) {
        getNotesCollection().add(note).await()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val NOTES_COLLECTION = "notes"
    }
}
