package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.PickupLineType
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
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

    val pickupLines: StateFlow<List<PickupLine>> = thePlaybookRepo.feedPickupLines.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    fun onVoteClick(pickupLineIndex: Int, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLineType = PickupLineType.FEED,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFavoriteBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(
                pickupLineIndex = pickupLineIndex,
                pickupLineType = PickupLineType.FEED
            )
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            _loggedInUiState.update { oldState ->
                oldState.copy(isRefreshing = true)
            }

            thePlaybookRepo.cleanPickupLineList(pickupLineType = PickupLineType.FEED)
            _loggedInUiState.update { oldState ->
                oldState.copy(currentPage = 0)
            }
            val (nPickupLinesReturned, message) =
                thePlaybookRepo.getPickupLineList(pickupLineType = PickupLineType.FEED)
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
                oldState.copy(currentPage = oldState.currentPage + 1)
            }
            val (nPickupLinesReturned, message) = thePlaybookRepo.getPickupLineList(
                pickupLineType = PickupLineType.FEED,
                page = _loggedInUiState.value.currentPage
            )
            thePlaybookRepo.emitMessage(message)

            _loggedInUiState.update { oldState ->
                oldState.copy(isLoadingNewItems = false, canLoadNewItems = nPickupLinesReturned > 0)
            }
        }
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

    fun onTitleTFChange(newTitle: String) {
        _loggedInUiState.update { oldState ->
            oldState.copy(pickupLineTitleCreate = newTitle)
        }
    }

    fun onContentTFChange(newContent: String) {
        _loggedInUiState.update { oldState ->
            oldState.copy(pickupLineContentCreate = newContent)
        }
    }

    fun onTagNameChangeValue(newTagName: String) {
        _loggedInUiState.update { oldState ->
            oldState.copy(tagNameCreate = newTagName)
        }
    }

    fun onTagDescriptionChangeValue(newTagDescription: String) {
        _loggedInUiState.update { oldState ->
            oldState.copy(tagDescriptionCreate = newTagDescription)
        }
    }

    fun onAddedTagsItemClick(index: Int) {
        _loggedInUiState.update { oldState ->
            val newTags = oldState.tagsToAdd.toMutableList()
            newTags.removeAt(index)
            oldState.copy(tagsToAdd = newTags)
        }
    }

    fun onTagCreateBtnClick() {
        viewModelScope.launch {
            if (loggedInHomeUiState.value.tagNameCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Tag name is blank (it's empty or consists solely of whitespace characters")
                return@launch
            }

            if (loggedInHomeUiState.value.tagDescriptionCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Tag description is blank (it's empty or consists solely of whitespace characters")
                return@launch
            }

            _loggedInUiState.update { oldState ->
                oldState.copy(isTagCreationLoading = true)
            }

            val result = _loggedInUiState.value.let {
                thePlaybookRepo.createTag(
                    name = it.tagNameCreate,
                    description = it.tagDescriptionCreate
                )
            }

            result.onSuccess {
                thePlaybookRepo.emitMessage("Tag successfully created")
                _loggedInUiState.update { oldState ->
                    val newTags = oldState.tagsToAdd.toMutableList()
                    newTags.add(result.getOrNull()!!)

                    oldState.copy(tagsToAdd = newTags)
                }

                onCancelTagCreateBtnClick()
            }.onFailure {
                thePlaybookRepo.emitMessage(result.exceptionOrNull()!!.message)
            }

            _loggedInUiState.update { oldState ->
                oldState.copy(isTagCreationLoading = false)
            }
        }
    }

    fun onCancelTagCreateBtnClick() {
        _loggedInUiState.update { oldState ->
            oldState.copy(
                tagNameCreate = "",
                tagDescriptionCreate = "",
                isTagCreatorVisible = false
            )
        }
    }

    fun onAddTagBtnClick() {
        with(_loggedInUiState) {
            if (!value.isTagCreatorVisible)
                update { oldState ->
                    oldState.copy(isTagCreatorVisible = true)
                }
            else
                update { oldState ->
                    oldState.copy(isTagCreatorVisible = false)
                }
        }
    }

    fun onSearchedTagChipClick(clickedTag: Tag) {
        _loggedInUiState.update { oldState ->
            val newTags = if (clickedTag in oldState.tagsToAdd)
                oldState.tagsToAdd.remove(clickedTag)
            else
                oldState.tagsToAdd.add(clickedTag)

            oldState.copy(tagsToAdd = newTags)
        }
    }

    fun onSearchTagBtnClick() {
        if (!_loggedInUiState.value.isTagSearcherVisible) {
            viewModelScope.launch {
                // Show SearchCard composable
                _loggedInUiState.update { oldState ->
                    oldState.copy(isTagSearcherVisible = true, isSearchingTags = true)
                }

                val result = thePlaybookRepo.getTags()
                result.onSuccess {
                    _loggedInUiState.update { oldState ->
                        oldState.copy(searchedTags = result.getOrNull()!!)
                    }
                }.onFailure {

                }

                _loggedInUiState.update { oldState ->
                    oldState.copy(isSearchingTags = false)
                }
            }
        } else
            _loggedInUiState.update { oldState ->
                oldState.copy(isTagSearcherVisible = false)
            }
    }

    fun onVisibilityCheckedChange(newVisibilityValue: Boolean) {
        _loggedInUiState.update { oldState ->
            oldState.copy(pickupLineVisibilityCreate = newVisibilityValue)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onPostBtnClick() {
        _loggedInUiState.update { oldState ->
            oldState.copy(isPostCreationLoading = true)
        }

        viewModelScope.launch {
            val result = with(_loggedInUiState.value) {
                thePlaybookRepo.createPickupLine(
                    title = pickupLineTitleCreate,
                    content = pickupLineContentCreate,
                    tagIds = tagsToAdd.map { tag -> tag.id },
                    isVisible = pickupLineVisibilityCreate,
                )
            }

            result.onSuccess {
                thePlaybookRepo.emitMessage(it)
            }.onFailure {
                thePlaybookRepo.emitMessage(it.message)
            }

            _loggedInUiState.update { oldState ->
                oldState.copy(isPostCreationLoading = false)
            }
        }
    }
}

private fun <T> List<T>.add(item: T): List<T> {
    val newList = this.toMutableList()
    newList.add(item)
    return newList
}

private fun <T> List<T>.remove(item: T): List<T> {
    val newList = this.toMutableList()
    newList.remove(item)
    return newList
}
