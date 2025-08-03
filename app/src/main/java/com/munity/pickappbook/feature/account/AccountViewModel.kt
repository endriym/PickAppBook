package com.munity.pickappbook.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.PickupLineType
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.model.PickupLine
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

    val personalPickupLines: StateFlow<List<PickupLine>> =
        thePlaybookRepo.personalPickupLines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val favoritePickupLines: StateFlow<List<PickupLine>> =
        thePlaybookRepo.favoritePickupLines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onPersonalPLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isPersonalRefreshing = true)
            }

            val (nPickupLinesReturned, message) = thePlaybookRepo.getPickupLineList(pickupLineType = PickupLineType.PERSONAL)
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isPersonalRefreshing = false,
                    personalCurrentPage = 0,
                    canLoadNewPersonalItems = nPickupLinesReturned > 0
                )
            }
        }
    }

    fun onLastPersonalPLReached() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewPersonalItems = true,
                    personalCurrentPage = oldState.personalCurrentPage + 1
                )
            }

            val (nPickupLinesReturned, message) = thePlaybookRepo.getPickupLineList(
                pickupLineType = PickupLineType.FAVORITE,
                page = _accountUiState.value.personalCurrentPage
            )
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewPersonalItems = false,
                    canLoadNewPersonalItems = nPickupLinesReturned > 0
                )
            }

        }
    }

    fun onPersonalPLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(pickupLineIndex, PickupLineType.PERSONAL)
        }
    }

    fun onPersonalPLVoteClick(pickupLineIndex: Int, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLineType = PickupLineType.PERSONAL,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLVoteClick(pickupLineIndex: Int, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLineType = PickupLineType.FAVORITE,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(
                pickupLineIndex,
                pickupLineType = PickupLineType.FAVORITE
            )
        }
    }

    fun onFavoritePLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isFavoriteRefreshing = true)
            }

            val (nPickupLinesReturned, message) = thePlaybookRepo.getPickupLineList(pickupLineType = PickupLineType.FAVORITE)
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isFavoriteRefreshing = false,
                    favoriteCurrentPage = 0,
                    canLoadNewFavoriteItems = nPickupLinesReturned > 0
                )
            }
        }
    }

    fun onLastFavoritePLReached() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewFavoriteItems = true,
                    favoriteCurrentPage = oldState.favoriteCurrentPage + 1
                )
            }

            val (nPickupLinesReturned, message) = thePlaybookRepo.getPickupLineList(
                pickupLineType = PickupLineType.FAVORITE,
                page = _accountUiState.value.favoriteCurrentPage
            )
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewFavoriteItems = false,
                    canLoadNewFavoriteItems = nPickupLinesReturned > 0
                )
            }
        }
    }

    fun onLogoutBtnClick() {
        viewModelScope.launch {
            thePlaybookRepo.logout()
        }
    }
}
