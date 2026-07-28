package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import okik.tech.jetcert.api.GitHubApi
import okik.tech.jetcert.api.NewsApi
import okik.tech.jetcert.api.NewsResponse
import okik.tech.jetcert.apollo.SearchTopReposQuery
import okik.tech.jetcert.db.Database
import okik.tech.jetcert.db.News
import okik.tech.jetcert.db.TopRepo
import kotlin.time.Clock


class MainViewModel(private val database: Database) : ViewModel() {

//    private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
//    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    val uiState: StateFlow<UiState>
            field = MutableStateFlow(UiState(false ))

    val newsApi: NewsApi = NewsApi()
    val gitHubApi: GitHubApi = GitHubApi()

    fun toggleShowingContent() = uiState.update { state -> state.copy(showContent = !state.showContent) }

    suspend fun getStories() {
        val topStories = newsApi.getTopStories()

        println("Top stories are ${topStories.map(NewsResponse.Story::title)}")

        uiState.update { uiState ->
            uiState.copy(
                showContent = true,
                topStories
            )
        }
    }

    suspend fun getTopRepos() {
        val repos = gitHubApi
            .apolloClient
            .query(SearchTopReposQuery())
            .execute()
            .dataAssertNoErrors
            .search
            .repos

        uiState.update {
            UiState(
                showContent = true,
                topRepos = repos?.filterNotNull()
            )
        }
    }

    suspend fun getNewsFromDB() : Flow<List<News>> =
        database.newsQueries.selectAll().asFlow().mapToList(Dispatchers.Default)

    suspend fun insertNewsToDB() {
        val news = newsApi.getTopStories()
        val newsQueries = database.newsQueries

        database.transaction {
            news.forEach { story ->
                newsQueries.upsert(
                    id = story.id,
                    type = story.type,
                    time = story.time,
                    by_ = story.by,
                    title = story.title,
                    score = story.score,
                    url = story.url,
                    descendants = story.descendants,
                    text = story.text
                )
            }
        }

        uiState.update {
            UiState(showContent = true)
        }
    }

    suspend fun insertFakeNews(title: String) {
        database.newsQueries.upsert(
            id = null,
            type = "story",
            time = Clock.System.now().epochSeconds,
            by_ = "Fake",
            title = title,
            score = 1000000,
            url = "",
            descendants = 0,
            text = title
        )

        database.newsQueries.upsert(
            id = 2,
            type = "story",
            time = Clock.System.now().epochSeconds,
            by_ = "Fake",
            title = "Updated",
            score = 1000000,
            url = "",
            descendants = 0,
            text = "updated"
        )
    }

    suspend fun getTopReposFromDb(): Flow<List<TopRepo>> =
        database.repoQueries.selectAll().asFlow().mapToList(Dispatchers.Default)

    suspend fun insertTopRepos() {
        val searchResponse =
            gitHubApi
                .apolloClient
                .query(SearchTopReposQuery())
                .execute()
                .dataAssertNoErrors
                .search
                .repos
                .orEmpty()
                .filterNotNull()

        database.transaction {
            searchResponse.forEach { search ->
                val repo = search.repo!!.onRepository!!

                database.repoQueries.upsert(
                    id = null,
                    url = repo.url,
                    name = repo.name,
                    stargazerCount = repo.stargazerCount,
                    createdAt = repo.createdAt.epochSeconds,
                    updatedAt = repo.updatedAt.epochSeconds
                )
            }
        }
    }
}

data class UiState(
    val showContent: Boolean,
    val stories: List<NewsResponse.Story>? = null,
    val topRepos: List<SearchTopReposQuery.Repo>? = null
)