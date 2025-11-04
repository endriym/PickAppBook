package com.munity.pickappbook.feature.home.loggedin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.ui.components.SortType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoggedInHomeViewModel(private val thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication
                LoggedInHomeViewModel(application.thePlaybookRepository)
            }
        }
    }

    private val _loggedInUiState = MutableStateFlow<LoggedInHomeUIState>(LoggedInHomeUIState())
    val loggedInHomeUiState: StateFlow<LoggedInHomeUIState> = _loggedInUiState.asStateFlow()

    val pickupLines: StateFlow<List<PickupLine>> =
        thePlaybookRepo.localFeedPickupLines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun onVoteClick(pickupLineId: String, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineId = pickupLineId,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoriteBtnClick(pickupLineId: String) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(pickupLineId = pickupLineId)
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            _loggedInUiState.update { oldState ->
                oldState.copy(isRefreshing = true)
            }

            thePlaybookRepo.cleanFeedPLs()
            _loggedInUiState.update { oldState ->
                oldState.copy(currentPage = 0)
            }
            val (nPickupLinesReturned, message) = thePlaybookRepo.getFeedPickupLines(sortType = _loggedInUiState.value.sortTypeSelected)
            thePlaybookRepo.emitMessage(message)

            _loggedInUiState.update { oldState ->
                oldState.copy(isRefreshing = false, canLoadNewItems = nPickupLinesReturned > 0)
            }
        }
    }

    fun onLastPickupLineReached() {
        viewModelScope.launch {
            _loggedInUiState.update { oldState ->
                oldState.copy(isLoadingNewItems = true)
            }

            _loggedInUiState.update { oldState ->
                if (oldState.currentPage == null)
                    oldState.copy(currentPage = 0)
                else
                    oldState.copy(currentPage = oldState.currentPage + 1)
            }
            val (nPickupLinesReturned, message) = thePlaybookRepo.getFeedPickupLines(
                sortType = _loggedInUiState.value.sortTypeSelected,
                page = _loggedInUiState.value.currentPage
            )
            thePlaybookRepo.emitMessage(message)

            _loggedInUiState.update { oldState ->
                oldState.copy(isLoadingNewItems = false, canLoadNewItems = nPickupLinesReturned > 0)
            }
        }
    }

    fun onSortChipClick(sortTypeClicked: SortType) {
        _loggedInUiState.update { oldState ->
            oldState.copy(
                sortTypeSelected = if (sortTypeClicked == _loggedInUiState.value.sortTypeSelected)
                    null
                else
                    sortTypeClicked
            )
        }

        onPullToRefresh()
    }

    fun onFABClick() {
        _loggedInUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = true)
        }
    }

    fun onBottomSheetDismiss() {
        _loggedInUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = false)
        }
    }
}
