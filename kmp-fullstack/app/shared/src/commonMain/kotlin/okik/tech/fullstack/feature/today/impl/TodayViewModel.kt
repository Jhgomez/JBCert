package okik.tech.fullstack.feature.today.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okik.tech.fullstack.domain.Apod
import okik.tech.fullstack.domain.ApodRepository
import okik.tech.fullstack.domain.DomainResult

data class TodayApodUiState(
    val apod: Apod? = null,
    val isLoading: Boolean = false,
    val businessLogicError: DomainResult.DomainErrorResult? = null
)

class TodayViewModel(
    private val apodRepository: ApodRepository
): ViewModel() {
    val uiState: StateFlow<TodayApodUiState>
        field = MutableStateFlow<TodayApodUiState>(TodayApodUiState())

    fun getTodayApod() {
        viewModelScope.launch {
            uiState.emit(uiState.value.copy(isLoading = true))

            val result = apodRepository.getTodayApod()

            uiState.emit(
                when(result) {
                    is DomainResult.Success<Apod> ->
                        TodayApodUiState(apod = result.result)
                    is DomainResult.DomainErrorResult ->
                        TodayApodUiState(businessLogicError = result)
                }
            )
        }
    }
}