package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val uiState: StateFlow<UiState>
            field = MutableStateFlow(UiState(false , Greeting().greet()))

    fun toggelShowingContent() = uiState.update { state -> state.copy(shoeContent = !state.shoeContent) }
}

data class UiState(val shoeContent: Boolean, val greeting: String)