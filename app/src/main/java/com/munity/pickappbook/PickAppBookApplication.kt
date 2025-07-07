package com.munity.pickappbook

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.munity.pickappbook.core.data.local.datastore.PickAppPreferencesDataSource
import com.munity.pickappbook.core.data.remote.ThePlaybookApi
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

private const val DATA_STORE_PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_PREFERENCES_NAME
)

class PickAppBookApplication : Application() {
    private lateinit var pickAppPreferencesDataSource: PickAppPreferencesDataSource
    private lateinit var thePlaybookApi: ThePlaybookApi
    lateinit var thePlaybookRepository: ThePlaybookRepository
    lateinit var applicationCoroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        applicationCoroutineScope = CoroutineScope(context = Dispatchers.IO)

        pickAppPreferencesDataSource = PickAppPreferencesDataSource(dataStore = dataStore)
        thePlaybookApi = ThePlaybookApi(preferencesStorage = pickAppPreferencesDataSource)
        thePlaybookRepository = ThePlaybookRepository(
            parentScope = applicationCoroutineScope,
            pickAppPrefsDS = pickAppPreferencesDataSource,
            thePlaybookApi = thePlaybookApi
        )
    }

    override fun onTerminate() {
        super.onTerminate()

        applicationCoroutineScope.cancel()
    }
}
