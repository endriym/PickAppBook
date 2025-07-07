package com.munity.pickappbook.feature.home.loggedout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoggedOutHomeViewModel(private val thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as PickAppBookApplication
                LoggedOutHomeViewModel(application.thePlaybookRepository)
            }
        }
    }

    private val _loggedOutUiState = MutableStateFlow(LoggedOutHomeUIState())
    val loggedOutUiState: StateFlow<LoggedOutHomeUIState> = _loggedOutUiState.asStateFlow()

    fun onUsernameLoginTFChange(newUsernameLogin: String) {
        _loggedOutUiState.update { oldState ->
            oldState.copy(usernameLogin = newUsernameLogin)
        }
    }

    fun onPasswordLoginTFChange(newPasswordLogin: String) {
        _loggedOutUiState.update { oldState ->
            oldState.copy(passwordLogin = newPasswordLogin)
        }
    }

    fun onUsernameCreateTFChange(newUsernameCreate: String) {
        _loggedOutUiState.update { oldState ->
            oldState.copy(usernameCreate = newUsernameCreate)
        }
    }

    fun onDisplayNameCreateTFChange(newDisplayNameCreate: String) {
        _loggedOutUiState.update { oldState ->
            oldState.copy(displayNameCreate = newDisplayNameCreate)
        }
    }

    fun onPasswordCreateTFChange(newPasswordCreate: String) {
        _loggedOutUiState.update { oldState ->
            oldState.copy(passwordCreate = newPasswordCreate)
        }
    }

    fun onLoginBtnClick() {
        viewModelScope.launch {
            if (_loggedOutUiState.value.usernameLogin.isBlank() || _loggedOutUiState.value.usernameLogin.isEmpty()) {
                thePlaybookRepo.emitMessage("Please enter a username")
                return@launch
            }

            if (_loggedOutUiState.value.passwordLogin.isBlank() || _loggedOutUiState.value.passwordLogin.isEmpty()) {
                thePlaybookRepo.emitMessage("Please enter a password")
                return@launch
            }

            // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
            _loggedOutUiState.update { oldState ->
                oldState.copy(isLoading = true)
            }

            val message = with(_loggedOutUiState.value) {
                thePlaybookRepo.login(
                    username = usernameLogin, password = passwordLogin
                )
            }
            thePlaybookRepo.emitMessage(message)

            // Enable 'Login' and 'Sign Up' button, make the progress indicator invisible
            _loggedOutUiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
        }
    }

    fun onImagePicked(newImageByteArray: ByteArray?) {
        if (newImageByteArray != null) {
            _loggedOutUiState.update { oldState ->
                oldState.copy(imageByteArray = newImageByteArray)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    fun onRemoveImagePicked() {
        _loggedOutUiState.update { oldState ->
            oldState.copy(imageByteArray = null)
        }
    }

    fun onCreateUserBtnClick() {
        viewModelScope.launch {
            if (_loggedOutUiState.value.usernameCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Please enter a username")
                return@launch
            }

            if (_loggedOutUiState.value.passwordCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Please enter a password")
                return@launch
            }

            if (_loggedOutUiState.value.imageByteArray == null) {
                thePlaybookRepo.emitMessage("Please enter a profile picture")
                return@launch
            }

            // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
            _loggedOutUiState.update { oldState ->
                oldState.copy(isLoading = true)
            }

            var message = "Trying to sign up..."
            thePlaybookRepo.emitMessage(message)

            message = with(_loggedOutUiState.value) {
                thePlaybookRepo.signup(
                    username = usernameCreate,
                    displayName = displayNameCreate,
                    password = passwordCreate,
                    profilePicture = imageByteArray!!
                )
            }
            thePlaybookRepo.emitMessage(message)

            // Enable 'Login' and 'Sign Up' button, make the progress indicator invisible
            _loggedOutUiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
        }
    }
}
