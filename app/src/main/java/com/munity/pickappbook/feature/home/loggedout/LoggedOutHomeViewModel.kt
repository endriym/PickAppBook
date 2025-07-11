package com.munity.pickappbook.feature.home.loggedout

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.util.StringsUtil
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
        if (StringsUtil.isValidUsername(newUsernameCreate)) {
            _loggedOutUiState.update { oldState ->
                oldState.copy(
                    usernameCreate = newUsernameCreate,
                    usernameCreateSupportingText = "",
                    isUsernameInvalid = false
                )
            }
        } else {
            _loggedOutUiState.update { oldState ->
                oldState.copy(
                    usernameCreate = newUsernameCreate,
                    usernameCreateSupportingText = "Please enter a valid email",
                    isUsernameInvalid = true
                )
            }
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
            val message = with(_loggedOutUiState.value) {
                if (usernameLogin.isBlank())
                    return@with "Please enter a username"

                if (passwordLogin.isBlank())
                    return@with "Please enter a password"

                // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
                _loggedOutUiState.update { oldState ->
                    oldState.copy(isLoading = true)
                }

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
            val message = with(_loggedOutUiState.value) {
                if (imageByteArray == null)
                    return@with "Please pick a profile picture"

                if (!StringsUtil.isValidUsername(usernameCreate))
                    return@with "Please enter a valid username"

                if (Regex("""\s*""").containsMatchIn(passwordCreate))
                    return@with "Please enter a valid password: it cannot be blank or contain any spaces"

                // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
                _loggedOutUiState.update { oldState ->
                    oldState.copy(isLoading = true)
                }
                thePlaybookRepo.emitMessage("Trying to sign up...")

                thePlaybookRepo.signup(
                    username = usernameCreate,
                    displayName = displayNameCreate.trim(),
                    password = passwordCreate,
                    profilePicture = imageByteArray
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
