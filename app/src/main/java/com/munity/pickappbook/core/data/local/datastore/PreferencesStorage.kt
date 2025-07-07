package com.munity.pickappbook.core.data.local.datastore

import com.munity.pickappbook.core.data.local.datastore.model.StoredPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesStorage {
    val storedPreferences: Flow<StoredPreferences>

    suspend fun saveNewUser(username: String, displayName: String, password: String)
    suspend fun saveDisplayName(newDisplayName: String)
    suspend fun savePassword(newPassword: String)
    suspend fun saveAccessToken(newAccessToken: String, expiration: String)
}
