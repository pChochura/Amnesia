package com.pointlessapps.amnesia.firebase.datasource.notes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.amnesia.datasource.notes.StoreNotesDataSource
import com.pointlessapps.amnesia.datasource.notes.dto.CategoryDto
import com.pointlessapps.amnesia.datasource.notes.dto.NoteDto
import kotlinx.coroutines.tasks.await

internal class StoreNotesDataSourceImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : StoreNotesDataSource {

    private fun getUserCollection() = firestore
        .collection(USERS_COLLECTION)
        .document(
            requireNotNull(auth.currentUser).uid,
        )

    private fun getNotesCollection() = getUserCollection()
        .collection(NOTES_COLLECTION)

    private fun getCategoriesCollection() = getUserCollection()
        .collection(CATEGORIES_COLLECTION)

    override suspend fun getAllCategories(): List<CategoryDto> =
        getCategoriesCollection().get().await().toObjects(CategoryDto::class.java)

    override suspend fun getAllNotes(): List<NoteDto> =
        getNotesCollection().get().await().toObjects(NoteDto::class.java)

    override suspend fun saveNote(note: NoteDto) {
        getNotesCollection().add(note).await()
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val NOTES_COLLECTION = "notes"
        const val CATEGORIES_COLLECTION = "categories"
    }
}
