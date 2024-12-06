package com.example.in2out.viewmodel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first


object ButtonPreferences {
    val LAST_CLICK_TIME = longPreferencesKey("last_click_time")
    val IN_TIME = stringPreferencesKey("in_time")
    val OUT_TIME = stringPreferencesKey("out_time")
    val BUTTON_ENABLED = booleanPreferencesKey("button_enabled")
}

object DataStoreHelper {


    suspend fun saveData(
        dataStore: DataStore<Preferences>,
        inTime: String,
        outTime: String,
        timestamp: Long,
        isButtonEnabled: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[ButtonPreferences.IN_TIME] = inTime
            preferences[ButtonPreferences.OUT_TIME] = outTime
            preferences[ButtonPreferences.LAST_CLICK_TIME] = timestamp
            preferences[ButtonPreferences.BUTTON_ENABLED] = isButtonEnabled
        }
    }


    suspend fun getLastClickTime(dataStore: DataStore<Preferences>): Long {
        val preferences = dataStore.data.first()
        return preferences[ButtonPreferences.LAST_CLICK_TIME] ?: 0L
    }


    suspend fun getInTime(dataStore: DataStore<Preferences>): String {
        val preferences = dataStore.data.first()
        return preferences[ButtonPreferences.IN_TIME] ?: ""
    }


    suspend fun getOutTime(dataStore: DataStore<Preferences>): String {
        val preferences = dataStore.data.first()
        return preferences[ButtonPreferences.OUT_TIME] ?: ""
    }


    suspend fun isButtonEnabled(dataStore: DataStore<Preferences>): Boolean {
        val preferences = dataStore.data.first()
        return preferences[ButtonPreferences.BUTTON_ENABLED] ?: true
    }
}
