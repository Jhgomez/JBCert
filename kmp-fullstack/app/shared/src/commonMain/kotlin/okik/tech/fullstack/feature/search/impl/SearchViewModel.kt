package okik.tech.fullstack.feature.search.impl

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult

data class SearchUiState(
    val apod: Apod? = null,
    val isLoading: Boolean = false,
    val error: DomainResult.DomainErrorResult? = null,
    // since this is user input and we will use it to persist the state of the app across system-
    // initiated process death we will store it here instead of near the composable that consumes it
    val localDate: LocalDate? = null
)

class SearchViewModel(
    private val apodRepository: ApodRepository
): ViewModel() {

    var state by mutableStateOf(SearchUiState())
        private set


    fun getApod(date: LocalDate) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, localDate = date)

            when (val response = apodRepository.getApodByDate(date)) {
                is DomainResult.Success<Apod> -> {
                    state = state.copy(
                        apod = response.result,
                        isLoading = false,
                        error = null
                    )
                }
                is DomainResult.DomainErrorResult -> {
                    state = state.copy(isLoading = false, error = response)
                }
            }
        }
    }
}