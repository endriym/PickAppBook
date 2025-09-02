package com.munity.pickappbook.feature.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.remote.ThePlaybookEndpoints
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val thePlaybookRepo: ThePlaybookRepository,
    private val userId: String,
) : ViewModel() {
    companion object {
        fun factoryWithUsername(userId: String): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication)
                    AccountViewModel(
                        thePlaybookRepo = application.thePlaybookRepository,
                        userId = userId
                    )
                }
            }
    }

    private val _accountUiState = MutableStateFlow(AccountUIState())
    val accountUiState: StateFlow<AccountUIState> = _accountUiState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> =
        thePlaybookRepo.isLoggedIn.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val loggedInUser: StateFlow<User?> = combine(
        thePlaybookRepo.loggedInUserId,
        thePlaybookRepo.loggedInUsername,
        thePlaybookRepo.loggedInDisplayName
    ) { id, username, displayName ->
        if (id != null && username != null && displayName != null)
            User(
                id = id,
                username = username,
                displayName = displayName,
                profilePictureUrl = ThePlaybookEndpoints.USER_IMAGE_ENDPOINT.format(username)
            )
        else
            null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    val postedPickupLines: StateFlow<List<PickupLine>> =
        thePlaybookRepo.getLocalPostedPickupLinesFlow(userId = userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val favoritePickupLines: StateFlow<List<PickupLine>> =
        thePlaybookRepo.localFavoritePickupLines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val isLoggedInUser: StateFlow<Boolean> = loggedInUser.map { it?.id == userId }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = false
    )

    val user: StateFlow<User?> = isLoggedInUser.map {
        if (it) {
            val id = thePlaybookRepo.loggedInUserId.first()
            val username = thePlaybookRepo.loggedInUsername.first()
            val displayName = thePlaybookRepo.loggedInDisplayName.first()

            if (id != null && username != null && displayName != null)
                User(
                    id = id,
                    username = username,
                    displayName = displayName,
                    profilePictureUrl = ThePlaybookEndpoints.USER_IMAGE_ENDPOINT.format(
                        username
                    )
                )
            else
                null
        } else {
            val result = thePlaybookRepo.getUserInfoById(userId)

            if (result.isSuccess)
                result.getOrNull()!!.let { userInfo ->
                    User(
                        id = userInfo.id,
                        username = userInfo.username,
                        displayName = userInfo.displayName,
                        profilePictureUrl = ThePlaybookEndpoints.USER_IMAGE_ENDPOINT.format(
                            userInfo.username
                        )
                    )
                }
            else
                null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    fun onPostedPLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isPostedRefreshing = true)
            }

            thePlaybookRepo.cleanPostedPLs(userId = userId)
            val (nPickupLinesReturned, message) = thePlaybookRepo.getPostedPickupLines(userId = userId)
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isPostedRefreshing = false,
                    postedCurrentPage = 0,
                    canLoadNewPostedItems = nPickupLinesReturned > 0
                )
            }
        }
    }

    fun onLastPostedPLReached() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewPostedItems = true,
                    postedCurrentPage = oldState.postedCurrentPage + 1
                )
            }

            val (nPickupLinesReturned, message) = thePlaybookRepo.getPostedPickupLines(
                userId = userId,
                page = _accountUiState.value.postedCurrentPage
            )
            thePlaybookRepo.emitMessage(message)

            _accountUiState.update { oldState ->
                oldState.copy(
                    isLoadingNewPostedItems = false,
                    canLoadNewPostedItems = nPickupLinesReturned > 0
                )
            }

        }
    }

    fun onPostedEditPLClick(pickupLine: PickupLine) {
        _accountUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = true, pickupLineToEditId = pickupLine.id)
        }
    }

    fun onPostedDeletePLClick(pickupLine: PickupLine) {
        deletePickupLine(pickupLine.id)
    }

    fun onPostedPLStarredBtnClick(pickupLineId: String) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(pickupLineId)
        }
    }

    fun onPostedPLVoteClick(pickupLineId: String, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineId = pickupLineId,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoritePLVoteClick(pickupLineId: String, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineId = pickupLineId,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoriteEditPLClick(pickupLine: PickupLine) {
        _accountUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = true, pickupLineToEditId = pickupLine.id)
        }
    }

    fun onFavoriteDeletePLClick(pickupLine: PickupLine) {
        deletePickupLine(pickupLine.id)
    }

    fun onFavoritePLStarredBtnClick(pickupLineId: String) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(
                pickupLineId = pickupLineId,
            )
        }
    }

    fun onFavoritePLRefresh() {
        viewModelScope.launch {
            _accountUiState.update { oldState ->
                oldState.copy(isFavoriteRefreshing = true)
            }

            thePlaybookRepo.cleanFavoritePLs()
            val (nPickupLinesReturned, message) = thePlaybookRepo.getFavoritePickupLines()
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

            val (nPickupLinesReturned, message) = thePlaybookRepo.getFavoritePickupLines(
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

    fun onBottomSheetDismiss() {
        _accountUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = false)
        }
    }

    private fun deletePickupLine(pickupLineId: String) {
        viewModelScope.launch {
            val wasDeleted = thePlaybookRepo.deletePickupLine(pickupLineId)

            if (wasDeleted) {
                thePlaybookRepo.deleteLocalPickupLine(pickupLineId)
            }
        }
    }
}
