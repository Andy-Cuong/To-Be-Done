package com.example.tobedone.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.example.tobedone.ui.screen.SortOption
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val IS_DETAIL_EXPANDED = booleanPreferencesKey("is_detail_expanded")
        val SORT_OPTION = intPreferencesKey("sort_option")
        const val TAG = "UserPreferenceRepo" // Log tag
    }

    val isDetailExpanded = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading if detail is expanded")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[IS_DETAIL_EXPANDED] ?: false }

    val sortOption = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading sort option")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val sortOptionOrdinal = preferences[SORT_OPTION] ?: 2
            when (sortOptionOrdinal) {
                0 -> SortOption.CREATION_TIME
                1 -> SortOption.UPDATE_TIME
                else -> SortOption.PRIORITY
            }
        }

    suspend fun saveDetailExpanded(isDetailExpanded: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DETAIL_EXPANDED] = isDetailExpanded
        }
    }

    suspend fun saveSortOption(option: SortOption) {
        dataStore.edit { preferences ->
            preferences[SORT_OPTION] = option.ordinal
        }
    }
}