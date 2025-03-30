package com.munity.pickappbook.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(private val thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PickAppBookApplication)
                HomeViewModel(application.thePlaybookRepository)
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

    fun onUsernameLoginTFChange(newUsernameLogin: String) {
        _uiState.update { oldState ->
            oldState.copy(usernameLogin = newUsernameLogin)
        }
    }

    fun onPasswordLoginTFChange(newPasswordLogin: String) {
        _uiState.update { oldState ->
            oldState.copy(passwordLogin = newPasswordLogin)
        }
    }

    fun onUsernameCreateTFChange(newUsernameCreate: String) {
        _uiState.update { oldState ->
            oldState.copy(usernameLogin = newUsernameCreate)
        }
    }

    fun onPasswordCreateTFChange(newPasswordCreate: String) {
        _uiState.update { oldState ->
            oldState.copy(passwordLogin = newPasswordCreate)
        }
    }

    fun onLoginBtnClick() {
        TODO()
    }

    fun onCreateUserBtnClick() {
        TODO()
    }

    fun onDismissSnackBar() {
        _uiState.update { newUiState ->
            newUiState.copy(isSnackbarVisible = false, snackbarMessage = "")
        }
    }
}