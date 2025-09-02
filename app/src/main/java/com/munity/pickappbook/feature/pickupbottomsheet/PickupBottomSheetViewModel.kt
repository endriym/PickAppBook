package com.munity.pickappbook.feature.pickupbottomsheet

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.munity.pickappbook.PickAppBookApplication
import com.munity.pickappbook.core.data.repository.ThePlaybookRepository
import com.munity.pickappbook.core.model.PickupLine
import com.munity.pickappbook.core.model.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PickupBottomSheetViewModel(
    private val thePlaybookRepo: ThePlaybookRepository,
    private val pickupLineToEditId: String? = null,
) : ViewModel() {
    companion object {
        fun factory(pickupLineToEditId: String? = null): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication
                    PickupBottomSheetViewModel(
                        thePlaybookRepo = application.thePlaybookRepository,
                        pickupLineToEditId = pickupLineToEditId
                    )
                }
            }
    }

    private val _pickupBottomSheetUiState: MutableStateFlow<PickupBottomSheetUIState> =
        MutableStateFlow(PickupBottomSheetUIState())
    val pickupBottomSheetUiState: StateFlow<PickupBottomSheetUIState> = _pickupBottomSheetUiState

    val isEditMode = pickupLineToEditId != null

    init {
        viewModelScope.launch {
            if (pickupLineToEditId == null) {
                _pickupBottomSheetUiState.update { PickupBottomSheetUIState() }
            } else {
                val pickupLineToEdit: PickupLine =
                    thePlaybookRepo.getLocalPickupLine(pickupLineToEditId)!!

                with(pickupLineToEdit) {
                    _pickupBottomSheetUiState.update { oldState ->
                        oldState.copy(
                            pickupLineTitleCreate = title,
                            pickupLineContentCreate = content,
                            tagsToAdd = tags ?: emptyList(),
                            pickupLineVisibilityCreate = isVisible
                        )
                    }
                }
            }
        }
    }

    fun onTitleTFChange(newTitle: String) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(pickupLineTitleCreate = newTitle)
        }
    }

    fun onContentTFChange(newContent: String) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(pickupLineContentCreate = newContent)
        }
    }

    fun onTagNameChangeValue(newTagName: String) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(tagNameCreate = newTagName)
        }
    }

    fun onTagDescriptionChangeValue(newTagDescription: String) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(tagDescriptionCreate = newTagDescription)
        }
    }

    fun onAddedTagsItemClick(index: Int) {
        _pickupBottomSheetUiState.update { oldState ->
            val newTags = oldState.tagsToAdd.toMutableList()
            newTags.removeAt(index)
            oldState.copy(tagsToAdd = newTags)
        }
    }

    fun onTagCreateBtnClick() {
        viewModelScope.launch {
            if (_pickupBottomSheetUiState.value.tagNameCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Tag name is blank (it's empty or consists solely of whitespace characters")
                return@launch
            }

            if (_pickupBottomSheetUiState.value.tagDescriptionCreate.isBlank()) {
                thePlaybookRepo.emitMessage("Tag description is blank (it's empty or consists solely of whitespace characters")
                return@launch
            }

            _pickupBottomSheetUiState.update { oldState ->
                oldState.copy(isTagCreationLoading = true)
            }

            val result = _pickupBottomSheetUiState.value.let {
                thePlaybookRepo.createTag(
                    name = it.tagNameCreate,
                    description = it.tagDescriptionCreate
                )
            }

            result.onSuccess {
                thePlaybookRepo.emitMessage("Tag successfully created")
                _pickupBottomSheetUiState.update { oldState ->
                    val newTags = oldState.tagsToAdd.toMutableList()
                    newTags.add(result.getOrNull()!!)

                    oldState.copy(tagsToAdd = newTags)
                }

                onCancelTagCreateBtnClick()
            }.onFailure {
                thePlaybookRepo.emitMessage(result.exceptionOrNull()!!.message)
            }

            _pickupBottomSheetUiState.update { oldState ->
                oldState.copy(isTagCreationLoading = false)
            }
        }
    }

    fun onCancelTagCreateBtnClick() {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(
                tagNameCreate = "",
                tagDescriptionCreate = "",
                isTagCreatorVisible = false
            )
        }
    }

    fun onAddTagBtnClick() {
        with(_pickupBottomSheetUiState) {
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
        _pickupBottomSheetUiState.update { oldState ->
            val newTags = if (clickedTag in oldState.tagsToAdd)
                oldState.tagsToAdd.remove(clickedTag)
            else
                oldState.tagsToAdd.add(clickedTag)

            oldState.copy(tagsToAdd = newTags)
        }
    }

    fun onSearchTagBtnClick() {
        if (!_pickupBottomSheetUiState.value.isTagSearcherVisible) {
            viewModelScope.launch {
                // Show SearchCard composable
                _pickupBottomSheetUiState.update { oldState ->
                    oldState.copy(isTagSearcherVisible = true, isSearchingTags = true)
                }

                val result = thePlaybookRepo.getTags()
                result.onSuccess {
                    _pickupBottomSheetUiState.update { oldState ->
                        oldState.copy(searchedTags = result.getOrNull()!!)
                    }
                }.onFailure {

                }

                _pickupBottomSheetUiState.update { oldState ->
                    oldState.copy(isSearchingTags = false)
                }
            }
        } else
            _pickupBottomSheetUiState.update { oldState ->
                oldState.copy(isTagSearcherVisible = false)
            }
    }

    fun onVisibilityCheckedChange(newVisibilityValue: Boolean) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(pickupLineVisibilityCreate = newVisibilityValue)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun onPostBtnClick(onActionComplete: () -> Unit) {
        _pickupBottomSheetUiState.update { oldState ->
            oldState.copy(isPostCreationLoading = true)
        }

        viewModelScope.launch {
            val result = if (isEditMode) {
                with(_pickupBottomSheetUiState.value) {
                    thePlaybookRepo.updatePickupLine(
                        pickupLineId = pickupLineToEditId!!,
                        newTitle = pickupLineTitleCreate,
                        newContent = pickupLineContentCreate,
                        newTagIds = tagsToAdd.map { tag -> tag.id },
                        newIsVisible = pickupLineVisibilityCreate
                    )
                }
            } else {
                with(_pickupBottomSheetUiState.value) {
                    thePlaybookRepo.createPickupLine(
                        title = pickupLineTitleCreate,
                        content = pickupLineContentCreate,
                        tagIds = tagsToAdd.map { tag -> tag.id },
                        isVisible = pickupLineVisibilityCreate,
                    )
                }
            }

            result.onSuccess {
                if (isEditMode) {
                    thePlaybookRepo.updateLocalPickupLine(it as PickupLine)
                } else
                    thePlaybookRepo.emitMessage(it as String)
            }.onFailure {
                thePlaybookRepo.emitMessage(it.message)
            }

            _pickupBottomSheetUiState.update { oldState ->
                oldState.copy(isPostCreationLoading = false)
            }

            onActionComplete()
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
