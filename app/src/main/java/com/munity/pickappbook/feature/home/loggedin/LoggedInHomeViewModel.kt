package com.munity.pickappbook.feature.home.loggedin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.remote.model.PickupLineResponse
import com.munity.pickappbook.core.data.remote.model.TagResponse
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    val pickupLines = thePlaybookRepo.pickupLines

    fun onVoteClick(pickupLineIndex: Int, newVote: PickupLineResponse.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updateStarred(pickupLineIndex)
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch {
            _loggedInUiState.update { oldState ->
                oldState.copy(isRefreshing = true)
            }

            val message = thePlaybookRepo.getPickupLineFeed()
            thePlaybookRepo.emitMessage(message)

            _loggedInUiState.update { oldState ->
                oldState.copy(isRefreshing = false)
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
        _loggedInUiState.update { oldState ->
            oldState.copy(isTagCreatorVisible = true)
        }
    }

    fun onSearchedTagChipClick(clickedTag: TagResponse) {
        _loggedInUiState.update { oldState ->
            val newTags = if (clickedTag in oldState.tagsToAdd)
                oldState.tagsToAdd.remove(clickedTag)
            else
                oldState.tagsToAdd.add(clickedTag)

            oldState.copy(tagsToAdd = newTags)
        }
    }

    fun onSearchTagBtnClick() {
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
    }

    fun onVisibilityCheckedChange(newVisibilityValue: Boolean) {
        _loggedInUiState.update { oldState ->
            oldState.copy(pickupLineVisibilityCreate = newVisibilityValue)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onPostBtnClick() {
        _loggedInUiState.update { oldState ->
            oldState.copy(isLoading = true)
        }

        viewModelScope.launch {
            val result = _loggedInUiState.value.let {
                thePlaybookRepo.createPickupLine(
                    title = it.pickupLineTitleCreate,
                    content = it.pickupLineContentCreate,
                    tagIds = it.tagsToAdd.map { tag -> tag.id },
                    isVisible = it.pickupLineVisibilityCreate,
                    isStarred = false,
                )
            }

            result.onSuccess {
                thePlaybookRepo.emitMessage(it)
            }.onFailure {
                thePlaybookRepo.emitMessage(it.message)
            }

            _loggedInUiState.update { oldState ->
                oldState.copy(isLoading = false)
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
