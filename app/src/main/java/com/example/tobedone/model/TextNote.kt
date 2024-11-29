package com.example.tobedone.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
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

enum class Priority(
    @StringRes val priorityText: Int,
    val color: Color
) {
    CRITICAL(priorityText = R.string.critical_priority, color = Color.Red),
    NEED_ATTENTION(priorityText = R.string.need_attention_priority, color = Color(0xFFFF9900)),
    DEFAULT(priorityText = R.string.default_priority, color = Color.Yellow),
    OPTIONAL(priorityText = R.string.optional_priority, color = Color.Cyan),
    TRIVIAL(priorityText = R.string.trivial_priority, color = Color.Green)
}