package com.munity.pickappbook.feature.search

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.munity.pickappbook.core.model.User
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(FlowPreview::class)
class SearchViewModel(private val thePlaybookRepo: ThePlaybookRepository) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PickAppBookApplication
                SearchViewModel(application.thePlaybookRepository)
            }
        }
    }

    private val _searchUiState = MutableStateFlow<SearchUIState>(SearchUIState())
    val searchUiState: StateFlow<SearchUIState> = _searchUiState

    val searchedPickupLines: List<PickupLine> = thePlaybookRepo.searchedPickupLines

    private lateinit var _allTags: List<Tag>

    private val _filterTags: SnapshotStateList<Tag> = mutableStateListOf()
    val filterTags: List<Tag> = _filterTags

    private var _matchingTags: MutableStateFlow<List<Tag>> =
        MutableStateFlow(listOf())
    val matchingTags: StateFlow<List<Tag>> = _matchingTags

    var searchedUsers: List<User> = listOf()
        private set

    private val _userQuery = MutableStateFlow("")
    val userQuery: StateFlow<String> = _userQuery.asStateFlow()

    private val _tagFilterQuery = MutableStateFlow("")
    val tagFilterQuery: StateFlow<String> = _tagFilterQuery

    init {
        viewModelScope.launch {
            _userQuery.debounce(200).collect { userQuery ->
                val result = thePlaybookRepo.getUsers(username = userQuery)
                result.onSuccess { userList ->
                    searchedUsers = userList
                }
            }
        }

        viewModelScope.launch {
            _allTags = thePlaybookRepo.getTags().getOrNull()!!

            _tagFilterQuery.debounce(200).collect { tagQuery ->
                _matchingTags.update {
                    _allTags.filter { tag ->
                        tag.name.contains(tagQuery, ignoreCase = true)
                                || tag.description.contains(tagQuery, ignoreCase = true)
                    }
                }

                if (_matchingTags.value.isNotEmpty() && _tagFilterQuery.value.isNotEmpty()) {
                    _searchUiState.update { oldState ->
                        oldState.copy(isTagDropdownExpanded = true)
                    }
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) =
        _searchUiState.update { oldState -> oldState.copy(query = newQuery) }

    fun onTrailingClearIconClick() =
        _searchUiState.update { oldState -> oldState.copy(query = "") }

    fun onFilterBtnClick() =
        _searchUiState.update { oldState -> oldState.copy(isBottomSheetVisible = true) }

    fun onSearch() {
        viewModelScope.launch {
            _searchUiState.update { oldState ->
                oldState.copy(isSearching = true)
            }

            thePlaybookRepo.cleanPickupLineList(pickupLineType = PickupLineType.FEED)
            val (nPickupLinesReturned, message) = with(_searchUiState.value) {
                when (queryType) {
                    QueryType.CONTENT -> thePlaybookRepo.getPickupLineList(
                        pickupLineType = PickupLineType.SEARCH,
                        content = query.ifBlank { null },
                        starred = isFavoriteQuery,
                        tagIds = _filterTags.map { it.id },
                        userId = userFilter?.id,
                        successPercentage = sliderValue?.roundToInt()?.div(100.0),
                    )

                    QueryType.TITLE -> thePlaybookRepo.getPickupLineList(
                        pickupLineType = PickupLineType.SEARCH,
                        title = query.ifBlank { null },
                        starred = isFavoriteQuery,
                        tagIds = _filterTags.map { it.id },
                        userId = userFilter?.id,
                        successPercentage = sliderValue?.roundToInt()?.div(100.0),
                    )

                    QueryType.CONTENT_TITLE -> TODO()

                    else -> thePlaybookRepo.getPickupLineList(
                        pickupLineType = PickupLineType.SEARCH,
                        content = query.ifBlank { null },
                        starred = isFavoriteQuery,
                        tagIds = _filterTags.map { it.id },
                        userId = userFilter?.id,
                        successPercentage = sliderValue?.roundToInt()?.div(100.0),
                    )
                }
            }
            thePlaybookRepo.emitMessage(message)

            _searchUiState.update { oldState ->
                oldState.copy(
                    isSearching = false,
                    isSearchComplete = true,
                    canLoadNewItems = nPickupLinesReturned > 0
                )
            }
        }
    }

    fun onPLStarredBtnClick(pickupLineIndex: Int) {
        viewModelScope.launch {
            thePlaybookRepo.updatePLFavoriteProperty(pickupLineIndex, PickupLineType.SEARCH)
        }
    }

    fun onPLVoteClick(pickupLineIndex: Int, newVote: PickupLine.Reaction.Vote) {
        viewModelScope.launch {
            val message = thePlaybookRepo.updateVote(
                pickupLineIndex = pickupLineIndex,
                pickupLineType = PickupLineType.SEARCH,
                newVote = newVote
            )

            thePlaybookRepo.emitMessage(message)
        }
    }

    fun onFilterChipClick(filterType: FilterType) {
        when (filterType) {
            FilterType.FAVORITE -> _searchUiState.update {
                it.copy(isFavoriteQuery = null)
            }

            FilterType.QUERY -> _searchUiState.update {
                it.copy(queryType = QueryType.CONTENT)
            }

            FilterType.SUCCESS_PERCENTAGE -> _searchUiState.update {
                it.copy(sliderValue = 0f)
            }

            FilterType.USER -> _searchUiState.update {
                it.copy(userFilter = null)
            }
        }
    }

    fun onTagFilterChipClick(tagClicked: Tag) = _filterTags.remove(tagClicked)

    /* SearchBottomSheet methods */

    fun onBottomSheetDismiss() =
        _searchUiState.update { oldState -> oldState.copy(isBottomSheetVisible = false) }

    fun onQueryTypeChange(newQueryType: QueryType) =
        _searchUiState.update { oldState ->
            oldState.copy(queryType = if (oldState.queryType == newQueryType) null else newQueryType)
        }

    fun onIsFavoriteQueryChange(isFavoriteQuery: Boolean) =
        _searchUiState.update { oldState -> oldState.copy(isFavoriteQuery = isFavoriteQuery) }

    fun onSliderValueChange(newValue: Float) =
        _searchUiState.update { oldState ->
            oldState.copy(sliderValue = if (newValue != 0f) newValue else null)
        }

    fun onTagFilterQueryChange(newTagFilterQuery: String) =
        _tagFilterQuery.update { newTagFilterQuery }

    fun onFilterTagClick(index: Int) = _filterTags.removeAt(index)

    fun onDropdownItemTagClick(index: Int) {
        val clickedTag = _matchingTags.value[index]

        if (clickedTag in _filterTags)
            _filterTags.remove(clickedTag)
        else
            _filterTags.add(clickedTag)

        _tagFilterQuery.update { "" }
        onTagDropdownDismissRequest()
    }

    fun onTagDropdownDismissRequest() =
        _searchUiState.update { oldState -> oldState.copy(isTagDropdownExpanded = false) }

    fun onRemoveUserSelected() =
        _searchUiState.update { oldState -> oldState.copy(userFilter = null) }

    fun onSearchUserBtnClick() {
        if (!_searchUiState.value.isUserSearcherVisible) {
            _searchUiState.update { oldState ->
                oldState.copy(isUserSearcherVisible = true)
            }
        } else {
            _searchUiState.update { oldState ->
                oldState.copy(isUserSearcherVisible = false)
            }
        }
    }

    fun onUserQueryChange(newUserQuery: String) = _userQuery.update { newUserQuery }

    fun onSearchedUserClick(newUserFilter: User) {
        _searchUiState.update { oldState ->
            oldState.copy(userFilter = newUserFilter)
        }
    }

    fun onApplyFiltersClick() {
        _searchUiState.update { oldState ->
            oldState.copy(isBottomSheetVisible = false)
        }
        onSearch()
    }
}

enum class QueryType {
    CONTENT, TITLE, CONTENT_TITLE
}

enum class FilterType {
    FAVORITE, QUERY, SUCCESS_PERCENTAGE, USER
}
