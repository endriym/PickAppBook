package com.munity.pickappbook.core.data.local.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.munity.pickappbook.core.data.model.StoredPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PickAppPrefsDataSource(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USERNAME_KEY = stringPreferencesKey("user")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val EXPIRES_KEY = stringPreferencesKey("expires")
        private const val TAG = "PickAppPrefsDataSource"
    }

    val storedPreference: Flow<StoredPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else
                throw exception
        }.map { preferences ->
            StoredPreferences(
                user = preferences[USERNAME_KEY],
                password = preferences[PASSWORD_KEY],
                accessToken = preferences[ACCESS_TOKEN_KEY],
                expiration = preferences[EXPIRES_KEY]
            )
        }

    suspend fun saveNewUser(username: String, password: String) {
        dataStore.edit {preferences ->
            preferences[USERNAME_KEY] = username
            preferences[PASSWORD_KEY] = password
        }
    }

    suspend fun saveUsername(newUsername: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = newUsername
        }
    }

    suspend fun savePassword(newPassword: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = newPassword
        }
    }

    suspend fun saveAccessToken(newAccessToken: String, expires: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = newAccessToken
            preferences[EXPIRES_KEY] = expires
        }
    }
}
