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

data class State(
    val text1: String? = null,
    val text2: String? = null
)

class SearchViewModel(
    private val apodRepository: ApodRepository
): ViewModel() {

    var state by mutableStateOf(State())
        private set

    fun set() {
        state = state.copy(
            text1 = "Solar Eclipses and Culture",
            text2 = "Pretend you have never heard of a solar eclipse. The Sun’s behavior has been predictable your whole life. One day, you witness the sky transform as it does in today’s spliced image spanning two hours of the August 12, 2026 solar eclipse. The Sun disappears, leaving behind a bright, empty ring. What would you think had happened? Humans have interpreted eclipses in countless ways throughout history, embedding beliefs about connection, rebirth, or danger into culture. “Eclipse” comes from the Greek word “ékleipsis” meaning “abandonment”. In ancient Greece, the solar eclipse marked the anger of the gods and the Sun abandoning humanity. To the Diné people, this celestial alignment is a time of renewal. Out of respect and to avoid the danger of sunlight, the Diné stay inside until the Sun and Moon separate. The Batammariba people of Benin and Togo believe that the Sun and Moon fight during an eclipse, so the community encourages peace among themselves. Eclipses are an example of the longstanding connection between astronomy and society.   Gallery: Solar Eclipse of 2026 August 12"
        )
    }
}