package com.example.tobedone.model

import androidx.annotation.StringRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tobedone.R

@Entity(tableName = "text_note")
data class TextNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val isDone: Boolean,
    @ColumnInfo(name = "last_updated") val updateEpochSecond: Long,
    @ColumnInfo(name = "creation_time") val creationEpochSecond: Long,
    /**
     * The smaller the priority, the more important the task, minimum is 1
     */
    val priority: Int
)

enum class Priority(@StringRes val priorityText: Int) {
    CRITICAL(priorityText = R.string.critical_priority),
    NEED_ATTENTION(priorityText = R.string.need_attention_priority),
    DEFAULT(priorityText = R.string.default_priority),
    OPTIONAL(priorityText = R.string.optional_priority),
    TRIVIAL(priorityText = R.string.trivial_priority)
}