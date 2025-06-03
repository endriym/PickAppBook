package com.munity.pickappbook.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    val pickupLines: List<PickupLine> = thePlaybookRepo.pickupLines

    private val _snackbarMessage: MutableSharedFlow<String?> = MutableSharedFlow()
    val snackbarMessaage: SharedFlow<String?> = _snackbarMessage.asSharedFlow()

    val isLoggedIn: StateFlow<Boolean> = thePlaybookRepo.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

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
            oldState.copy(usernameCreate = newUsernameCreate)
        }
    }

    fun onPasswordCreateTFChange(newPasswordCreate: String) {
        _uiState.update { oldState ->
            oldState.copy(passwordCreate = newPasswordCreate)
        }
    }

    fun onLoginBtnClick() {
        viewModelScope.launch {
            if (_uiState.value.usernameLogin.isBlank() || _uiState.value.usernameLogin.isEmpty()) {
                _snackbarMessage.emit("Please enter a username")
                return@launch
            }

            if (_uiState.value.passwordLogin.isBlank() || _uiState.value.passwordLogin.isEmpty()) {
                _snackbarMessage.emit("Please enter a password")
                return@launch
            }

            // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
            _uiState.update { oldState ->
                oldState.copy(isLoading = true)
            }

            val message =
                thePlaybookRepo.login(_uiState.value.usernameLogin, _uiState.value.passwordLogin)
            _snackbarMessage.emit(message)

            // Enable 'Login' and 'Sign Up' button, make the progress indicator invisible
            _uiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
        }
    }

    fun onImagePicked(newImageByteArray: ByteArray?) {
        if (newImageByteArray != null) {
            _uiState.update { oldState ->
                oldState.copy(imageByteArray = newImageByteArray)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    fun onRemoveImagePicked() {
        _uiState.update { oldState ->
            oldState.copy(imageByteArray = null)
        }
    }

    fun onCreateUserBtnClick() {
        viewModelScope.launch {
            if (_uiState.value.usernameCreate.isBlank() || _uiState.value.usernameCreate.isEmpty()) {
                _snackbarMessage.emit("Please enter a username")
                return@launch
            }

            if (_uiState.value.passwordCreate.isBlank() || _uiState.value.passwordCreate.isEmpty()) {
                _snackbarMessage.emit("Please enter a password")
                return@launch
            }

            if (_uiState.value.imageByteArray == null) {
                _snackbarMessage.emit("Please enter a profile picture")
                return@launch
            }

            // Disable 'Login' and 'Sign Up' button, make the progress indicator visible
            _uiState.update { oldState ->
                oldState.copy(isLoading = true)
            }

            var message = "Trying to sign up..."
            _snackbarMessage.emit(message)

            message = thePlaybookRepo.signup(
                username = _uiState.value.usernameCreate,
                password = _uiState.value.passwordCreate,
                profilePicture = _uiState.value.imageByteArray!!
            )

            _snackbarMessage.emit(message)

            // Enable 'Login' and 'Sign Up' button, make the progress indicator invisible
            _uiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
        }
    }

    fun onVoteClick(pickupLineIndex: Int, newVote: PickupLine.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                newVote = newVote
            )

            _snackbarMessage.emit(message)
        }
    }

    fun onStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updateStarred(pickupLineIndex)
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            _uiState.update { oldState ->
                oldState.copy(isLoading = true)
            }

            val message = thePlaybookRepo.getPickupLineFeed()
            _snackbarMessage.emit(message)

            _uiState.update { oldState ->
                oldState.copy(isLoading = false)
            }
        }
    }
}


