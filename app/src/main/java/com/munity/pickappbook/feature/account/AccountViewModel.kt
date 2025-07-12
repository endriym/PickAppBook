package com.munity.pickappbook.feature.account

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(private val thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication)
                AccountViewModel(application.thePlaybookRepository)
            }
        }
    }

    private val _accountUiState = MutableStateFlow(AccountUIState())
    val accountUiState: StateFlow<AccountUIState> = _accountUiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = thePlaybookRepo.isLoggedIn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val currentUsername: StateFlow<String?> = thePlaybookRepo.currentUsername.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    val currentDisplayName: StateFlow<String?> = thePlaybookRepo.currentDisplayName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    private val _personalPickupLines: SnapshotStateList<PickupLineResponse> =
        mutableStateListOf<PickupLineResponse>()
    val personalPickupLines: List<PickupLineResponse> = _personalPickupLines

    private val _favoritePickupLines: SnapshotStateList<PickupLineResponse> =
        mutableStateListOf<PickupLineResponse>()
    val favoritePickupLines: List<PickupLineResponse> = _favoritePickupLines

    fun onPersonalPLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = true)
            }

            val message = thePlaybookRepo.getPersonalPickupLineList(_personalPickupLines)
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = false)
            }
        }
    }

    fun onPersonalPLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updateStarred(pickupLineIndex, _personalPickupLines)
        }
    }

    fun onPersonalPLVoteClick(pickupLineIndex: Int, newVote: PickupLineResponse.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLines = _personalPickupLines,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLVoteClick(pickupLineIndex: Int, newVote: PickupLineResponse.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLines = _favoritePickupLines,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updateStarred(pickupLineIndex, _favoritePickupLines)
        }
    }

    fun onFavoritePLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isFavoriteRefreshing = true)
            }

            val message = thePlaybookRepo.getFavoritePickupLineList(_favoritePickupLines)
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(isFavoriteRefreshing = false)
            }
        }
    }

    fun onLogoutBtnClick() {
        viewModelScope.launch {
            thePlaybookRepo.logout()
        }
    }
}
