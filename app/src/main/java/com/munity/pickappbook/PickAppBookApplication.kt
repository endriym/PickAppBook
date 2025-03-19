package com.munity.pickappbook

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository

private const val DATA_STORE_PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_PREFERENCES_NAME
)

class PickAppBookApplication : Application() {
    private lateinit var pickAppPrefsDataSource: PickAppPrefsDataSource
    private lateinit var thePlaybookRepository: ThePlaybookRepository

    override fun onCreate() {
        super.onCreate()

        pickAppPrefsDataSource = PickAppPrefsDataSource(dataStore = dataStore)
        thePlaybookRepository = ThePlaybookRepository(pickAppPrefsDS = pickAppPrefsDataSource)
    }
}