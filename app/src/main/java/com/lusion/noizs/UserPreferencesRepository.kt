package com.lusion.noizs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_ANCHORED_TO_BOTTOM = booleanPreferencesKey("is_anchored_to_bottom")
    }

    val isAnchoredToBottom: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ANCHORED_TO_BOTTOM] ?: false
    }

    suspend fun setAnchoredToBottom(isAnchored: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ANCHORED_TO_BOTTOM] = isAnchored
        }
    }
}

