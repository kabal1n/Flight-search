package com.example.flightsearch.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Создаём DataStore (расширение Context)
private val Context.dataStore by preferencesDataStore(name = "settings")

object SearchPreferences {

    // Ключ для сохранения строки поиска
    private val SEARCH_KEY = stringPreferencesKey("search_text")

    // Сохранение строки поиска
    suspend fun saveSearchQuery(context: Context, query: String) {
        context.dataStore.edit { preferences ->
            preferences[SEARCH_KEY] = query
        }
    }

    // Чтение строки поиска как Flow
    fun getSearchQuery(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[SEARCH_KEY] ?: ""
        }
    }
}
