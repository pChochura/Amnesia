package com.pointlessapps.amnesia.model

data class Note(
	val title: String?,
	val content: String,
	val createdAt: String,
	val updatedAt: String,
	val categories: Set<Category>,
	val isPinned: Boolean,
)
