package okik.tech.daggerexample

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okik.tech.daggerexample.domain.FakeRepoA
import javax.inject.Inject

data class ScreenAState(
    val repoAResult: String = ""
)

class DemoVm @Inject constructor(
    private val repoA: FakeRepoA
): ViewModel() {
    val screenAState = mutableStateOf(ScreenAState())

    fun executeRepoA() {
        screenAState.value = screenAState.value.copy(repoAResult = repoA.execute())
    }
}