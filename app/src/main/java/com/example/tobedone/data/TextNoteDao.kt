package com.example.tobedone.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tobedone.model.TextNote
import kotlinx.coroutines.flow.Flow

@Dao
interface TextNoteDao {
    @Insert
    suspend fun addNote(textNote: TextNote)

    @Update
    suspend fun updateNote(textNote: TextNote)

    @Delete
    suspend fun deleteNote(textNote: TextNote)

    @Query("SELECT * FROM text_note WHERE id = :id")
    fun getNote(id: Int): Flow<TextNote>

    @Query("SELECT * FROM text_note " +
            "WHERE (title LIKE '%' || :searchText || '%' " +
            "OR content LIKE '%' || :searchText || '%') " +
            "AND priority IN (:priorityFilter) " +
            "AND isDone IN (:isDoneFilter)" +
            "ORDER BY priority ASC")
    fun getNotesSortedByPriority(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

    @Query("SELECT * FROM text_note " +
            "WHERE (title LIKE '%' || :searchText || '%' " +
            "OR content LIKE '%' || :searchText || '%') " +
            "AND priority IN (:priorityFilter) " +
            "AND isDone IN (:isDoneFilter)" +
            "ORDER BY last_updated DESC")
    fun getNotesSortedByUpdateTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

    @Query("SELECT * FROM text_note " +
            "WHERE (title LIKE '%' || :searchText || '%' " +
            "OR content LIKE '%' || :searchText || '%') " +
            "AND priority IN (:priorityFilter) " +
            "AND isDone IN (:isDoneFilter)" +
            "ORDER BY creation_time DESC")
    fun getNotesSortedByCreationTime(
        searchText: String,
        priorityFilter: Set<Int>,
        isDoneFilter: Set<Boolean>
    ): Flow<List<TextNote>>

//    @Query("SELECT * FROM text_note " +
//            "WHERE priority IN (:priority) " +
//            "ORDER BY isDone ASC")
//    fun getNotesByPriority(vararg priority: Int): Flow<List<TextNote>>
//
//    @Query("SELECT * FROM text_note " +
//            "WHERE isDone IN (:status) " +
//            "ORDER BY priority ASC")
//    fun getNotesByIsDoneStatus(vararg status: Boolean): Flow<List<TextNote>>
}