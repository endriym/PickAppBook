package com.munity.pickappbook.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication
                HomeViewModel(application.thePlaybookRepository)
            }
        }
    }

    val snackbarMessages: SharedFlow<String?> = thePlaybookRepo.messages

    val isLoggedIn: StateFlow<Boolean> = thePlaybookRepo.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Companion.WhileSubscribed(),
        initialValue = false
    )
}