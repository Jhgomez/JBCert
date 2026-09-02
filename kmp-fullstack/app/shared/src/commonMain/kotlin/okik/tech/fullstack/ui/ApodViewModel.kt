package okik.tech.fullstack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult
import okik.tech.fullstack.domain.Paging
import okik.tech.fullstack.models.ApodResponse

data class HomeUiState(
    val isLoading: Boolean = false,
    val apodList: List<Apod> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val shouldShowRetry: Boolean = false,
)

data class TodayUiState(
    val isLoading: Boolean = false,
    val todayApod: Apod? = null,
    val error: String? = null
)

data class FindUiState(
    val isLoading: Boolean = false,
    val foundApod: Apod? = null,
    val error: String? = null,
    val searchDate: String = ""
)

class ApodViewModel(
    private val repository: ApodRepository
) : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _todayUiState = MutableStateFlow(TodayUiState())
    val todayUiState: StateFlow<TodayUiState> = _todayUiState.asStateFlow()

    private val _findUiState = MutableStateFlow(FindUiState())
    val findUiState: StateFlow<FindUiState> = _findUiState.asStateFlow()

    private val pageSize = 10

    init {
        loadApodHistory()
        loadTodayApod() // Also load today's APOD on init
    }

    fun loadApodHistory() {
        if (_homeUiState.value.isLoading) return

        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true, error = null)

            with(repository.getApodHistory(_homeUiState.value.currentPage, pageSize)) {
                when (this) {
                    is DomainResult.Success<Paging<Apod>> -> {
                        val currentList = _homeUiState.value.apodList
                        val newList = if (_homeUiState.value.currentPage == 1) {
                            result.items
                        } else {
                            currentList + result.items
                        }

                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            apodList = newList,
                            hasMorePages = result.page < result.totalPages,
                            currentPage = _homeUiState.value.currentPage + 1
                        )
                    }
                    is DomainResult.DomainErrorResult.UnknownResult,
                    is DomainResult.DomainErrorResult.NotFound,
                    is DomainResult.DomainErrorResult.NetworkError,
                    is DomainResult.DomainErrorResult.Unauthorized,
                    is DomainResult.DomainErrorResult.UnhandledHttpCode ->
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            error = "$this",
                            shouldShowRetry = this is DomainResult.DomainErrorResult.NetworkError
                        )

                }
            }
        }
    }

    fun refresh() {
        _homeUiState.value = HomeUiState()
        loadApodHistory()
    }

    fun loadMoreItems() {
        if (_homeUiState.value.hasMorePages && !_homeUiState.value.isLoading) {
            loadApodHistory()
        }
    }

    fun loadTodayApod() {
        viewModelScope.launch {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true, error = null)

            with(repository.getTodayApod()) {
                when (this) {
                    is DomainResult.Success<Apod> -> {
                        _todayUiState.value = _todayUiState.value.copy(
                            isLoading = false,
                            todayApod = result,
                            error = null
                        )
                    }
                    is DomainResult.DomainErrorResult.UnknownResult,
                    is DomainResult.DomainErrorResult.NotFound,
                    is DomainResult.DomainErrorResult.NetworkError,
                    is DomainResult.DomainErrorResult.Unauthorized,
                    is DomainResult.DomainErrorResult.UnhandledHttpCode ->
                        _homeUiState.value = _homeUiState.value.copy(
                            isLoading = false,
                            error = "$this",
                            shouldShowRetry = this is DomainResult.DomainErrorResult.NetworkError
                        )
                }
            }
        }
    }

    fun refreshTodayApod() {
        loadTodayApod()
    }

    fun updateSearchDate(date: String) {
        _findUiState.value = _findUiState.value.copy(
            searchDate = date,
            foundApod = null, // Clear previous result when date changes
            error = null
        )
    }

    fun searchApodByDate(date: String) {
        // Basic date validation
        if (!isValidDateFormat(date)) {
            _findUiState.value = _findUiState.value.copy(
                error = "Please enter a valid date in YYYY-MM-DD format"
            )
            return
        }

        viewModelScope.launch {
            _findUiState.value = _findUiState.value.copy(isLoading = true, error = null)

//            with(repository.getApodByDate(date)) {
//                when (this) {
//                    is DomainResult.Success<Apod> ->
//                        _findUiState.value = _findUiState.value.copy(
//                            isLoading = false,
//                            foundApod = result,
//                            error = null
//                        )
//                    is DomainResult.DomainErrorResult.NetworkError,
//                    is DomainResult.DomainErrorResult.NotFound,
//                    is DomainResult.DomainErrorResult.Unauthorized,
//                    is DomainResult.DomainErrorResult.UnhandledHttpCode,
//                    is DomainResult.DomainErrorResult.UnknownResult -> {
//
//                        _homeUiState.value = _homeUiState.value.copy(
//                            isLoading = false,
//                            error = "$this",
//                            shouldShowRetry = this is DomainResult.DomainErrorResult.NetworkError
//                        )
//                    }
//                }
//            }
        }
    }

    fun clearFindResults() {
        _findUiState.value = FindUiState()
    }

    private fun isValidDateFormat(date: String): Boolean {
        return try {
            // Basic regex check for YYYY-MM-DD format
            val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
            if (!regex.matches(date)) return false

            // Additional validation can be added here
            // For example, checking if the date is not in the future
            // and not before June 16, 1995 (first APOD date)

            true
        } catch (e: Exception) {
            false
        }
    }
}