package com.munity.pickappbook

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.munity.pickappbook.core.data.local.datastore.PickAppPrefsDataSource
import com.munity.pickappbook.core.data.remote.ThePlaybookDataSource
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

private const val DATA_STORE_PREFERENCES_NAME = "preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_PREFERENCES_NAME
)

class PickAppBookApplication : Application() {
    private lateinit var pickAppPrefsDataSource: PickAppPrefsDataSource
    private lateinit var thePlaybookDataSource: ThePlaybookDataSource
    lateinit var thePlaybookRepository: ThePlaybookRepository
    lateinit var applicationCoroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()

        applicationCoroutineScope = CoroutineScope(context = Dispatchers.IO)

        pickAppPrefsDataSource = PickAppPrefsDataSource(dataStore = dataStore)
        thePlaybookDataSource =
            ThePlaybookDataSource(pickAppPrefsDataSource = pickAppPrefsDataSource)
        thePlaybookRepository = ThePlaybookRepository(
            parentScope = applicationCoroutineScope,
            pickAppPrefsDS = pickAppPrefsDataSource,
            thePlaybookDS = thePlaybookDataSource
        )
    }

    override fun onTerminate() {
        super.onTerminate()

        applicationCoroutineScope.cancel()
    }
}