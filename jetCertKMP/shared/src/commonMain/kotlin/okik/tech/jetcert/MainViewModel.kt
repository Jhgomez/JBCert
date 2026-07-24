package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okik.tech.jetcert.api.GitHubApi
import okik.tech.jetcert.api.NewsApi
import okik.tech.jetcert.api.NewsResponse
import okik.tech.jetcert.apollo.generated.SearchTopReposQuery

class MainViewModel : ViewModel() {

//    private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val uiState: StateFlow<UiState>
            field = MutableStateFlow(UiState(false , emptyList()))

    val newsApi: NewsApi = NewsApi()
    val gitHubApi: GitHubApi = GitHubApi()

    fun toggelShowingContent() = uiState.update { state -> state.copy(showContent = !state.showContent) }

    suspend fun getStories() {
        val topStories = newsApi.getTopStories()

        println("Top stories are ${topStories.map(NewsResponse.Story::title)}")

        uiState.update { uiState ->
            uiState.copy(showContent = true, topStories)
        }
    }

    suspend fun getRepos() {
        val repus = gitHubApi.apolloClient.query(SearchTopReposQuery()).execute().data?.search?.repos
    }
}

data class UiState(val showContent: Boolean, val stories: List<NewsResponse.Story>)