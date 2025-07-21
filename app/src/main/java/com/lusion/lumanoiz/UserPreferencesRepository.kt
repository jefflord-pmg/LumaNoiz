package com.lusion.lumanoiz

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    companion object {
        private val LAST_SELECTED_SOUND = stringPreferencesKey("last_selected_sound")
        private val BALL_SIZE = floatPreferencesKey("ball_size")
        private val MIN_BALL_SPEED = intPreferencesKey("min_ball_speed")
        private val MAX_BALL_SPEED = intPreferencesKey("max_ball_speed")
        private val SESSION_DURATION = intPreferencesKey("session_duration")
        private val UI_ANCHORED_BOTTOM = stringPreferencesKey("ui_anchored_bottom")
    }

    val lastSelectedSound: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[LAST_SELECTED_SOUND] }

    val ballSize: Flow<Float> = context.dataStore.data
        .map { preferences -> preferences[BALL_SIZE] ?: 50f }

    val minBallSpeed: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[MIN_BALL_SPEED] ?: 1000 }

    val maxBallSpeed: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[MAX_BALL_SPEED] ?: 3000 }

    val sessionDuration: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[SESSION_DURATION] ?: 5 }

    val uiAnchoredBottom: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[UI_ANCHORED_BOTTOM] ?: "false" }

    suspend fun saveLastSelectedSound(sound: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SELECTED_SOUND] = sound
        }
    }

    suspend fun saveBallSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[BALL_SIZE] = size
        }
    }

    suspend fun saveBallSpeed(minSpeed: Int, maxSpeed: Int) {
        context.dataStore.edit { preferences ->
            preferences[MIN_BALL_SPEED] = minSpeed
            preferences[MAX_BALL_SPEED] = maxSpeed
        }
    }

    suspend fun saveSessionDuration(duration: Int) {
        context.dataStore.edit { preferences ->
            preferences[SESSION_DURATION] = duration
        }
    }

    suspend fun saveUiAnchoredBottom(anchored: String) {
        context.dataStore.edit { preferences ->
            preferences[UI_ANCHORED_BOTTOM] = anchored
        }
    }
}