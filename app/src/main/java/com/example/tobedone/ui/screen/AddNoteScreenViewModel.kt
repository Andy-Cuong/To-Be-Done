package com.example.tobedone.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tobedone.ToBeDoneApplication
import com.example.tobedone.data.TextNoteRepository
import com.example.tobedone.model.TextNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class AddNoteScreenViewModel(
    private val textNoteRepository: TextNoteRepository
) : ViewModel() {
    private val _addNoteScreenFlow = MutableStateFlow(AddNoteScreenState())
    val addNoteScreenFlow = _addNoteScreenFlow.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _addNoteScreenFlow.update { addNoteScreenState ->
            addNoteScreenState.copy(title = newTitle)
        }
    }

    fun onPriorityChange(newPriority: Int) {
        _addNoteScreenFlow.update { addNoteScreenState ->
            addNoteScreenState.copy(priority = newPriority)
        }
    }

    fun onContentChange(newContent: String) {
        _addNoteScreenFlow.update { addNoteScreenState ->
            addNoteScreenState.copy(content = newContent)
        }
    }

    fun onDone() {
        if (addNoteScreenFlow.value.title.isEmpty()) { // Use beginning part of content as title
            val subContentEnd = addNoteScreenFlow.value.content.indexOf(" ", startIndex = 12)
            val subContent = if (subContentEnd < 0) {
                addNoteScreenFlow.value.content
            } else {
                addNoteScreenFlow.value.content.substring(0, subContentEnd)
            }
            _addNoteScreenFlow.update { addNoteScreenState ->
                addNoteScreenState.copy(title = subContent)
            }
        }

        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val newNote = TextNote(
                title = addNoteScreenFlow.value.title.trim(),
                priority = addNoteScreenFlow.value.priority,
                content = addNoteScreenFlow.value.content.trim(),
                isDone = false,
                creationEpochSecond = LocalDateTime.now().atZone(zoneId).toEpochSecond(),
                updateEpochSecond = LocalDateTime.now().atZone(zoneId).toEpochSecond()
            )

            textNoteRepository.addNote(newNote)
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as ToBeDoneApplication
                val container = application.container
                AddNoteScreenViewModel(textNoteRepository = container.textNoteRepository)
            }
        }
    }
}

data class AddNoteScreenState(
    val title: String = "",
    val priority: Int = 3,
    val content: String = ""
)