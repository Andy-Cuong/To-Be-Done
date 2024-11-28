package com.example.tobedone.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tobedone.ToBeDoneApplication
import com.example.tobedone.data.TextNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class EditNoteScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val textNoteRepository: TextNoteRepository
) : ViewModel() {
    // Retrieve the clicked note from SavedStateHandle
    private val noteId: Int = checkNotNull(savedStateHandle[EditNoteDestination.NOTE_ID_ARG])

    private val _editNoteScreenFlow = MutableStateFlow(EditNoteScreenState())
    val editNoteScreenFlow = _editNoteScreenFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val currentNote = textNoteRepository.getNote(noteId)
                .filterNotNull()
                .first()

            _editNoteScreenFlow.update { editNoteScreenState ->
                editNoteScreenState.copy(
                    title = currentNote.title,
                    priority = currentNote.priority,
                    content = currentNote.content
                )
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _editNoteScreenFlow.update { editNoteScreenState ->
            editNoteScreenState.copy(title = newTitle)
        }
    }

    fun onPriorityChange(newPriority: Int) {
        _editNoteScreenFlow.update { editNoteScreenState ->
            editNoteScreenState.copy(priority = newPriority)
        }
    }

    fun onContentChange(newContent: String) {
        _editNoteScreenFlow.update { editNoteScreenState ->
            editNoteScreenState.copy(content = newContent)
        }
    }

    fun onDone() {
        if (editNoteScreenFlow.value.title.isEmpty()) { // Use beginning part of content as title
            val subContentEnd = editNoteScreenFlow.value.content.indexOf(" ", startIndex = 12)
            val subContent = if (subContentEnd < 0) {
                editNoteScreenFlow.value.content
            } else {
                editNoteScreenFlow.value.content.substring(0, subContentEnd)
            }
            _editNoteScreenFlow.update { editNoteScreenState ->
                editNoteScreenState.copy(title = subContent)
            }
        }

        viewModelScope.launch {
            val zoneId = ZoneId.systemDefault()
            val currentNote = textNoteRepository.getNote(noteId)
                .filterNotNull()
                .first()

            textNoteRepository.updateNote(
                currentNote.copy(
                    title = editNoteScreenFlow.value.title.trim(),
                    priority = editNoteScreenFlow.value.priority,
                    content = editNoteScreenFlow.value.content.trim(),
                    updateEpochSecond = LocalDateTime.now().atZone(zoneId).toEpochSecond()
                )
            )
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as ToBeDoneApplication
                val container = application.container
                EditNoteScreenViewModel(
                    savedStateHandle = this.createSavedStateHandle(),
                    textNoteRepository = container.textNoteRepository
                )
            }
        }
    }
}

data class EditNoteScreenState(
    val title: String = "",
    val priority: Int = 3,
    val content: String = ""
)