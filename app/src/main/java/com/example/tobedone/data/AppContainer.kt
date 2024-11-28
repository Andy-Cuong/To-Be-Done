package com.example.tobedone.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Dependencies container for the app
 */
interface AppContainer {
    val textNoteRepository: TextNoteRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(
    context: Context
) : AppContainer {
    override val textNoteRepository: TextNoteRepository by lazy {
        OfflineTextNoteRepository(ToBeDoneDatabase.getDatabase(context = context).getTextNoteDao())
    }

    private val userPreferencesName = "user_preferences"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = userPreferencesName
    )

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore = context.dataStore)
    }
}