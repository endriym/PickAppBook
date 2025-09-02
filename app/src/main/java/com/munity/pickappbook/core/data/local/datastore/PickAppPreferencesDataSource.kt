package com.munity.pickappbook.core.data.local.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.munity.pickappbook.core.data.local.datastore.model.StoredPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PickAppPreferencesDataSource(
    private val dataStore: DataStore<Preferences>,
) : PreferencesStorage {
    private companion object {
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val EXPIRATION_KEY = stringPreferencesKey("expiration")
        private const val TAG = "PickAppPrefsDataSource"
    }

    override val storedPreferences: Flow<StoredPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences", exception)
                emit(emptyPreferences())
            } else
                throw exception
        }.map { preferences ->
            StoredPreferences(
                userId = preferences[USER_ID_KEY],
                username = preferences[USERNAME_KEY],
                displayName = preferences[DISPLAY_NAME_KEY],
                password = preferences[PASSWORD_KEY],
                accessToken = preferences[ACCESS_TOKEN_KEY],
                expiration = preferences[EXPIRATION_KEY]
            )
        }

    override suspend fun saveNewUser(
        userId: String,
        username: String,
        displayName: String,
        password: String,
    ) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[DISPLAY_NAME_KEY] = displayName
            preferences[PASSWORD_KEY] = password
        }
    }

    override suspend fun saveNewDisplayName(newDisplayName: String) {
        dataStore.edit { preferences ->
            preferences[DISPLAY_NAME_KEY] = newDisplayName
        }
    }

    override suspend fun saveNewPassword(newPassword: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = newPassword
        }
    }

    override suspend fun saveNewAccessToken(newAccessToken: String, expiration: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = newAccessToken
            preferences[EXPIRATION_KEY] = expiration
        }
    }

    override suspend fun removeUser() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences.remove(DISPLAY_NAME_KEY)
            preferences.remove(PASSWORD_KEY)
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(EXPIRATION_KEY)
        }
    }
}
