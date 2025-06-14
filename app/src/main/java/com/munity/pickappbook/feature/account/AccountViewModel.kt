package com.munity.pickappbook.feature.account

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.model.PickupLine
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.data.repository.swapList
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

    private val _personalPickupLines: SnapshotStateList<PickupLine> =
        mutableStateListOf<PickupLine>()
    val personalPickupLines: List<PickupLine> = _personalPickupLines


    fun onPersonalPLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = true)
            }

            val result = thePlaybookRepo.getPickupLineList()
//            thePlaybookRepo.emitMessage(message)
            result.onSuccess {
                _personalPickupLines.swapList(it)
            }

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

    fun onPersonalPLVoteClick(pickupLineIndex: Int, newVote: PickupLine.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLines = _personalPickupLines,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updateStarred(pickupLineIndex, _personalPickupLines)
        }
    }

    fun onFavoritePLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = true)
            }

            val result = thePlaybookRepo.getPickupLineList()
//            thePlaybookRepo.emitMessage(message)
            result.onSuccess {
                _personalPickupLines.swapList(it)
            }

            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = false)
            }
        }
    }

    fun onLogoutBtnClick() {
        viewModelScope.launch {
            thePlaybookRepo.logout()
        }
    }
}