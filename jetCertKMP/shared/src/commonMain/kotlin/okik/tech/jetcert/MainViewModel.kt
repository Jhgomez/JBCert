package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}

data class UiState(val shoeContent: Boolean, val greeting: String)