package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.apollographql.apollo.ApolloClient
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeWriteMode
import eu.anifantakis.lib.ksafe.asMutableStateFlow
import eu.anifantakis.lib.ksafe.compose.mutableStateOf
import eu.anifantakis.lib.ksafe.invoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import okik.tech.jetcert.api.NewsApi
import okik.tech.jetcert.api.NewsResponse
import okik.tech.jetcert.apollo.SearchTopReposQuery
import okik.tech.jetcert.db.Database
import okik.tech.jetcert.db.News
import okik.tech.jetcert.db.TopRepo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.time.Clock

class MainViewModel(
    private val database: Database,
    private val gitHubApi: ApolloClient,
    private val settingsVault: KSafe
) : ViewModel(), KoinComponent {

    // private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
    // val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // for plain text only(no encryption)
    private val settingsPreferences: KSafe by inject(named("prefs"))

    // prefer constructor injection but just ot show case field injection, btw you have to implement
    // KoinComponent to do field injection
    private val newsApi: NewsApi by inject()

    // ====================== encrypted and plain tex settings =====================

    // The variable name is used as the storage key when no explicit key is supplied.
    private var hasShownOnboarding: Boolean by settingsPreferences(defaultValue = true, mode = KSafeWriteMode.Plain)
        private set // if var was public this would allows us to only update it form this class

    private val token by settingsVault("")  // if changed directly nobody is notified

    // I could use this variable inside a composable directly and watch recomposition happening by
    // chnaging it directly, but that would mess my screen state, so Ill make it private
    private var stars by settingsPreferences.mutableStateOf<UByte>(0U, mode = KSafeWriteMode.Plain)
        private set // if var was public this would allows us to only update it form this class

    val PERSON_KEY = "PERSON"
    val COUNTER = "COUNTER"

    private val _reactiveCounterOne by settingsPreferences.asMutableStateFlow<UByte>(0u, scope = viewModelScope, mode = KSafeWriteMode.Plain)
    val reactiveCounterOne = _reactiveCounterOne.asStateFlow()

    suspend fun savePerson(person: Person) = settingsPreferences.put(PERSON_KEY, person, mode = KSafeWriteMode.Plain)

    suspend fun getPerson() = settingsPreferences.get<Person?>(PERSON_KEY, null)

    // 6. Direct API — non-suspend, hot-cache reads, background-flushed writes (~1000x faster for bulk ops)
    fun getCounter() = settingsPreferences.getDirect<Byte>(COUNTER, 0)

    fun incrementCounter() = settingsPreferences.putDirect(
        COUNTER,
        getCounter() + 1,
        mode = KSafeWriteMode.Plain
    )

    fun decrementCounter() = settingsPreferences.putDirect(
        COUNTER,
        getCounter() - 1,
        mode = KSafeWriteMode.Plain
    )

    fun addStar() = stars++

    fun subtractStar() = stars--

    // ============================================================================

    val uiState: StateFlow<UiState>
        field = MutableStateFlow(
            UiState(
                showContent = false,
                hasShownOnboarding = hasShownOnboarding,
                token = token,
                stars = stars,
                counter = getCounter(),
                reactiveCounterOne = reactiveCounterOne.value,
                person = null
            )
        )

    suspend fun getStories() {
        val topStories = newsApi.getTopStories()

        println("Top stories are ${topStories.map(NewsResponse.Story::title)}")

        uiState.update { uiState ->
            uiState.copy(
                showContent = true,
//                topStories
            )
        }
    }

    suspend fun getTopRepos() {
        val repos = gitHubApi
            .query(SearchTopReposQuery())
            .execute()
            .dataAssertNoErrors
            .search
            .repos

        uiState.update {
            it.copy(showContent = true)
        }
    }

    init {
        viewModelScope.launch {
            database.newsQueries.selectAll().asFlow().mapToList(Dispatchers.Default).collect { news ->
                uiState.update {
                    it.copy(showContent = true, stories = news, topRepos = null)
                }
            }
        }

        viewModelScope.launch {
            database.repoQueries.selectAll().asFlow().mapToList(Dispatchers.Default).collect { repository ->
                uiState.update {
                    it.copy(showContent = true, stories = null, topRepos = repository)
                }
            }
        }

        viewModelScope.launch {
            uiState.update {
                it.copy(person = getPerson())
            }
        }
    }

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
    }

    suspend fun insertFakeNews(title: String) {
        database.newsQueries.upsert(
            id = null,
            type = "story",
            time = Clock.System.now().epochSeconds,
            by_ = "Fake",
            title = title,
            score = 1000001,
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

    suspend fun insertTopRepos() {
        val searchResponse =
            gitHubApi
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
    val stories: List<News>? = null,
    val topRepos: List<TopRepo>? = null,
    val hasShownOnboarding: Boolean,
    val token: String,
    val stars: UByte,
    val counter: Byte,
    val reactiveCounterOne: UByte,
    val person: Person?
)

@Serializable
data class Person(
    val name: String,
    val age: UByte
)
