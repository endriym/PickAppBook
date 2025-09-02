package com.munity.pickappbook.core.data.local.datastore

import com.munity.pickappbook.core.data.local.datastore.model.StoredPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesStorage {
    val storedPreferences: Flow<StoredPreferences>

    suspend fun saveNewUser(userId: String, username: String, displayName: String, password: String)
    suspend fun saveNewDisplayName(newDisplayName: String)
    suspend fun saveNewPassword(newPassword: String)
    suspend fun saveNewAccessToken(newAccessToken: String, expiration: String)
    suspend fun removeUser()
}
