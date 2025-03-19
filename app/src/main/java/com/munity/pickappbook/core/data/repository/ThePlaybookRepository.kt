package com.munity.pickappbook.core.data.repository

import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThePlaybookRepository(
    private val pickAppPrefsDS: PickAppPrefsDataSource,
) {
    val isLoggedIn: Flow<Boolean> = pickAppPrefsDS.storedPreference.map { storedPrefs ->
        !(storedPrefs.user.isNullOrEmpty() || storedPrefs.password.isNullOrEmpty())
    }

    suspend fun saveNewUser(username: String, password: String) =
        pickAppPrefsDS.saveNewUser(username, password)
}