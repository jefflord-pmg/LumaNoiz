package com.lusion.lumanoiz

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_ANCHORED_TO_BOTTOM = booleanPreferencesKey("is_anchored_to_bottom")
        val BALL_SIZE = floatPreferencesKey("ball_size")
        val MIN_DURATION = floatPreferencesKey("min_duration")
        val MAX_DURATION = floatPreferencesKey("max_duration")
        val TOTAL_DURATION_MINUTES = floatPreferencesKey("total_duration_minutes")
    }

    val isAnchoredToBottom: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_ANCHORED_TO_BOTTOM] ?: false
    }

    suspend fun setAnchoredToBottom(isAnchored: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ANCHORED_TO_BOTTOM] = isAnchored
        }
    }

    val ballSize: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.BALL_SIZE] ?: 0.1f
    }

    suspend fun setBallSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BALL_SIZE] = size
        }
    }

    val minDuration: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MIN_DURATION] ?: 100f
    }

    suspend fun setMinDuration(duration: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MIN_DURATION] = duration
        }
    }

    val maxDuration: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MAX_DURATION] ?: 2000f
    }

    suspend fun setMaxDuration(duration: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MAX_DURATION] = duration
        }
    }

    val totalDurationMinutes: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TOTAL_DURATION_MINUTES] ?: 10f
    }

    suspend fun setTotalDurationMinutes(minutes: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOTAL_DURATION_MINUTES] = minutes
        }
    }
}
