package com.example.tobedone.ui.screen

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tobedone.R
import com.example.tobedone.ToBeDoneApplication
import com.example.tobedone.data.TextNoteRepository
import com.example.tobedone.data.UserPreferencesRepository
import com.example.tobedone.model.Priority
import com.example.tobedone.model.TextNote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModel(
    private val textNoteRepository: TextNoteRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _homeScreenFlow = MutableStateFlow(HomeScreenState())
    val homeScreenFlow = _homeScreenFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(
                    sortedBy = userPreferencesRepository.sortOption
                        .filterNotNull()
                        .first(),
                    isDetailExpanded = userPreferencesRepository.isDetailExpanded
                        .filterNotNull()
                        .first()
                )
            }
        }

        // flatMapLatest observes the change of the homeScreenFlow. Whenever the flow changes/emits
        // a new value (e.g search value change, or sortedBy change), the flatMapLatest block will be
        // triggered, which return a Flow of data (List<TextNote> in this case) from the repo
        _homeScreenFlow.flatMapLatest { homeScreenState ->
            val priorityFilterToUse = homeScreenState.priorityFilter.ifEmpty {
                mutableSetOf<Int>().apply { // Apply all filter if filter set is empty
                    Priority.entries.forEach { option ->
                        add(option.ordinal + 1)
                    }
                }
            }

            val isDoneFilterToUse = homeScreenState.isDoneFilter.ifEmpty {
                setOf(true, false)
            }

            when (homeScreenState.sortedBy) {
                SortOption.CREATION_TIME -> textNoteRepository.getNotesSortedByCreationTime(
                    searchText = homeScreenState.searchValue,
                    priorityFilter = priorityFilterToUse,
                    isDoneFilter = isDoneFilterToUse
                )
                    .filterNotNull()
                SortOption.UPDATE_TIME -> textNoteRepository.getNotesSortedByUpdateTime(
                    searchText = homeScreenState.searchValue,
                    priorityFilter = priorityFilterToUse,
                    isDoneFilter = isDoneFilterToUse
                )
                    .filterNotNull()
                SortOption.PRIORITY -> textNoteRepository.getNotesSortedByPriority(
                    searchText = homeScreenState.searchValue,
                    priorityFilter = priorityFilterToUse,
                    isDoneFilter = isDoneFilterToUse
                )
                    .filterNotNull()
            }
        }.onEach { textNoteList -> // For each new emission, update the homeScreenFlow with the new data
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(textNoteList = textNoteList)
            }
        }.launchIn(viewModelScope) // and doing so inside the viewModelScope
    }

    fun onSearchValueChange(searchValue: String) {
        _homeScreenFlow.update { homeScreenState ->
            homeScreenState.copy(
                searchValue = searchValue
            )
        }
    }

    fun changeSortOption(newOption: SortOption) {
        _homeScreenFlow.update { homeScreenState ->
            homeScreenState.copy(sortedBy = newOption)
        }

        viewModelScope.launch {
            userPreferencesRepository.saveSortOption(option = newOption)
        }
    }

    fun toggleDetailExpanded() {
        _homeScreenFlow.update { homeScreenState ->
            homeScreenState.copy(isDetailExpanded = !homeScreenState.isDetailExpanded)
        }

        viewModelScope.launch {
            userPreferencesRepository.saveDetailExpanded(isDetailExpanded = homeScreenFlow.value.isDetailExpanded)
        }
    }

    fun onCheckBoxClicked(textNote: TextNote) {
        viewModelScope.launch {
            textNoteRepository.updateNote(textNote = textNote.copy(isDone = !textNote.isDone))
        }
    }

    fun deleteTextNote(textNote: TextNote) {
        viewModelScope.launch {
            textNoteRepository.deleteNote(textNote = textNote)
        }
    }

    fun addPriorityFilter(priority: Int) {
        if (homeScreenFlow.value.priorityFilter.contains(priority)) {
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(
                    priorityFilter = homeScreenState.priorityFilter.minus(priority)
                )
            }
        } else {
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(
                    priorityFilter = homeScreenState.priorityFilter.plus(priority)
                )
            }
        }
    }

    fun addIsDoneFilter(isDone: Boolean) {
        if (homeScreenFlow.value.isDoneFilter.contains(isDone)) {
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(
                    isDoneFilter = homeScreenState.isDoneFilter.minus(isDone)
                )
            }
        } else {
            _homeScreenFlow.update { homeScreenState ->
                homeScreenState.copy(
                    isDoneFilter = homeScreenState.isDoneFilter.plus(isDone)
                )
            }
        }
    }

    fun resetFilter() {
        _homeScreenFlow.update { homeScreenState ->
            homeScreenState.copy(
                priorityFilter = emptySet(),
                isDoneFilter = emptySet()
            )
        }
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as ToBeDoneApplication
                val container = application.container
                HomeScreenViewModel(
                    textNoteRepository = container.textNoteRepository,
                    userPreferencesRepository = container.userPreferencesRepository
                )
            }
        }
    }
}

data class HomeScreenState(
    val searchValue: String = "",
    val textNoteList: List<TextNote> = emptyList(),
    val sortedBy: SortOption = SortOption.UPDATE_TIME,
    val isDetailExpanded: Boolean = false,
    val priorityFilter: Set<Int> = emptySet(),
    val isDoneFilter: Set<Boolean> = emptySet()
)

enum class SortOption(@StringRes val optionText: Int) {
    CREATION_TIME(optionText = R.string.sort_by_creation_time),
    UPDATE_TIME(optionText = R.string.sort_by_update_time),
    PRIORITY(optionText = R.string.sort_by_priority)
}