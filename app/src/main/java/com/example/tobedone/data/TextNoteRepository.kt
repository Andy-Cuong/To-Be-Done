package com.example.tobedone.data

import com.example.tobedone.model.TextNote
import kotlinx.coroutines.flow.Flow

interface TextNoteRepository {
    suspend fun addNote(textNote: TextNote)

    suspend fun updateNote(textNote: TextNote)

    suspend fun deleteNote(textNote: TextNote)

    fun getNote(id: Int): Flow<TextNote>

    fun getNotesSortedByPriority(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

    fun getNotesSortedByUpdateTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

    fun getNotesSortedByCreationTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

//    fun getNotesByPriority(vararg priority: Int): Flow<List<TextNote>>
//
//    fun getNotesByIsDoneStatus(vararg status: Boolean): Flow<List<TextNote>>
}

class OfflineTextNoteRepository(
    private val textNoteDao: TextNoteDao
) : TextNoteRepository {
    override suspend fun addNote(textNote: TextNote) =
        textNoteDao.addNote(textNote)

    override suspend fun updateNote(textNote: TextNote) =
        textNoteDao.updateNote(textNote)

    override suspend fun deleteNote(textNote: TextNote) =
        textNoteDao.deleteNote(textNote)

    override fun getNote(id: Int): Flow<TextNote> =
        textNoteDao.getNote(id)

    override fun getNotesSortedByPriority(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>> = textNoteDao.getNotesSortedByPriority(
        searchText,
        priorityFilter,
        isDoneFilter
    )

    override fun getNotesSortedByUpdateTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>> = textNoteDao.getNotesSortedByUpdateTime(
        searchText,
        priorityFilter,
        isDoneFilter
    )

    override fun getNotesSortedByCreationTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>> = textNoteDao.getNotesSortedByCreationTime(
        searchText,
        priorityFilter,
        isDoneFilter
    )

//    override fun getNotesByPriority(vararg priority: Int): Flow<List<TextNote>> =
//        textNoteDao.getNotesByPriority(*priority)
//
//    override fun getNotesByIsDoneStatus(vararg status: Boolean): Flow<List<TextNote>> =
//        textNoteDao.getNotesByIsDoneStatus(*status)
}