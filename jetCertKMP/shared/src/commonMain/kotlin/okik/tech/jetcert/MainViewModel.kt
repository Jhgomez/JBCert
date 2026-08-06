package okik.tech.jetcert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.async.coroutines.awaitAsOne
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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
import kotlin.jvm.JvmInline
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class MainViewModel(
    private val database: Database,
    private val gitHubApi: ApolloClient,
    private val settingsVault: KSafe // you can delete all values with the funciton .clearAll()
) : ViewModel(), KoinComponent {

    // private val _uiState = MutableStateFlow(UiState(false, Greeting().greet()))
    // val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // for plain text only(no encryption)
    private val settingsPreferences: KSafe by inject(named("prefs"))

    // prefer constructor injection but just ot show case field injection, btw you have to implement
    // KoinComponent to do field injection
    private val newsApi: NewsApi by inject()

    private val tokenInput = MutableStateFlow("")

    // ====================== encrypted and plain tex settings =====================

    // The variable name is used as the storage key when no explicit key is supplied.
    private var hasShownOnboarding: Boolean by settingsPreferences(defaultValue = false, mode = KSafeWriteMode.Plain)
        private set // if var was public this would allows us to only update it form this class

    private var token by settingsVault("")  // if changed directly nobody is notified

    // I could use this variable inside a composable directly and watch recomposition happening by
    // chnaging it directly, but that would mess my screen state, so Ill make it private
    private var stars by settingsPreferences.mutableStateOf<UByte>(0U, mode = KSafeWriteMode.Plain)
        private set // if var was public this would allows us to only update it form this class

    val PERSON_KEY = "PERSON"
    val COUNTER = "COUNTER"

    private val _reactiveCounterOne by settingsPreferences.asMutableStateFlow<UByte>(0u, scope = viewModelScope, mode = KSafeWriteMode.Plain)
    val reactiveCounterOne = _reactiveCounterOne.asStateFlow()

    fun toggleHashShownOnboarding() {
        hasShownOnboarding = !hasShownOnboarding

        userState.update {
            it.copy(hasShownOnboarding = hasShownOnboarding)
        }
    }

    fun updateToken(input: String) {
        tokenInput.update { input }
    }

    fun incrementReactiveCounter() {
        _reactiveCounterOne.value++
    }

    fun decrementReactiveCounter() {
        _reactiveCounterOne.value--
    }

    fun savePerson(input: String): Boolean {
        val props = input.split(",")

        return if (props.size >= 2) {
            val age = props[1].trim().toUByteOrNull()

            if (age != null) {
                val person = Person(props[0], age)

                viewModelScope.launch {
                    settingsPreferences.put(PERSON_KEY, person, mode = KSafeWriteMode.Plain)

                    userState.update {
                        it.copy(person = getPerson())
                    }
                }

                true
            } else {
                false
            }
        } else {
            false
        }
    }

    suspend fun getPerson() = settingsPreferences.get<Person?>(PERSON_KEY, null)

    // 6. Direct API — non-suspend, hot-cache reads, background-flushed writes (~1000x faster for bulk ops)
    fun getCounter() = settingsPreferences.getDirect<Byte>(COUNTER, 0)

    fun incrementCounter() {
        settingsPreferences.putDirect(
            COUNTER,
            (getCounter() + 1).toByte(),
            mode = KSafeWriteMode.Plain
        )

        userState.update {
            it.copy(counter = getCounter())
        }
    }

    fun decrementCounter() {
        settingsPreferences.putDirect<Byte>(
            COUNTER,
            (getCounter() - 1).toByte(),
            mode = KSafeWriteMode.Plain
        )

        userState.update {
            it.copy(counter = getCounter())
        }
    }

    fun addStar() {
        val localStar = stars++

        userState.update {
            it.copy(stars = localStar)
        }
    }

    fun subtractStar() {
        val localStar = stars--

        userState.update {
            it.copy(stars = localStar)
        }
    }

    // ============================================================================

    private val reposFlow = database.repoQueries.selectAll().asFlow()
        .distinctUntilChanged()
        .mapToList(Dispatchers.Default)
        .map { DatabaseFlow.ReposFlow(it) }

    private val newsFlow = database.newsQueries.selectAll().asFlow()
        .distinctUntilChanged()
        .mapToList(Dispatchers.Default)
        .map { DatabaseFlow.NewsFlow(it) }

    private val dbFlow = merge(
        reposFlow.onStart { DatabaseFlow.ReposFlow(emptyList()) },
        newsFlow.onStart { DatabaseFlow.NewsFlow(emptyList()) }
    )

    // this all should be private, I'm only using it to showcase explicit backing fields
    val userState: StateFlow<UserState>
        field = MutableStateFlow(
            UserState(
                        showContent = false,
                        hasShownOnboarding = hasShownOnboarding,
                        token = token,
                        stars = stars,
                        counter = getCounter(),
                        person = null
            )
        )

    val uiState: StateFlow<UiState> = combine(
        dbFlow,
        userState,
        reactiveCounterOne
    ) { dbUpdates, userUpdates, counterUpdates ->

        val isRepo = dbUpdates is DatabaseFlow.ReposFlow

        UiState(
            showContent = userUpdates.showContent,
            hasShownOnboarding = userUpdates.hasShownOnboarding,
            token = userUpdates.token,
            stars = userUpdates.stars,
            counter = userUpdates.counter,
            reactiveCounterOne = counterUpdates,
            person = userUpdates.person,
            stories = if (isRepo) null else if(userUpdates.showContent)(dbUpdates as DatabaseFlow.NewsFlow).getValues() else null,
            topRepos = if (isRepo) if (userUpdates.showContent) dbUpdates.getValues() else null else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState(
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

        userState.update { userState ->
            userState.copy(
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

        userState.update {
            it.copy(showContent = true)
        }
    }

    init {
        viewModelScope.launch {
            userState.update {
                it.copy(person = getPerson())
            }
        }

        viewModelScope.launch {
            tokenInput
                .debounce(400)
                .filter { it.isNotBlank() }
                .distinctUntilChanged()
                .collect { input ->
                    token = input

                    userState.update {
                        it.copy(token = token)
                    }
                }
        }
    }

    suspend fun insertNewsToDB() {
        if (uiState.value.stories == null && uiState.value.topRepos == null ||
            uiState.value.stories != null) {
            userState.update {
                it.copy(showContent = !it.showContent)
            }
        }

        if (!userState.value.showContent || uiState.value.stories == null) {
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
        if (uiState.value.stories == null && uiState.value.topRepos == null ||
            uiState.value.topRepos != null) {
            userState.update {
                it.copy(showContent = !it.showContent)
            }
        }

        if (!userState.value.showContent || uiState.value.topRepos == null) {
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

    fun addFourHours(id: Long) {
        viewModelScope.launch {
            val result = database.newsQueries.select(id).awaitAsOne()
//
            if (result != null) {
                val time = Instant.fromEpochSeconds(result.time).plus(4.hours).epochSeconds

                database.newsQueries.upsert(
                    id = result.id,
                    type = result.type,
                    time = time,
                    by_ = result.by_,
                    title = result.title,
                    score = result.score,
                    url = result.url,
                    descendants = result.descendants,
                    text = result.text
                )
            }
        }
    }
}

sealed interface DatabaseFlow<T> {
    fun getValues(): List<T>

    @JvmInline
    value class NewsFlow(private val news: List<News>): DatabaseFlow<News> {
        override fun getValues(): List<News> = news
    }

    @JvmInline
    value class ReposFlow(private val repos: List<TopRepo>): DatabaseFlow<TopRepo> {
        override fun getValues(): List<TopRepo> = repos
    }
}

data class UiState(
    val stories: List<News>? = null,
    val topRepos: List<TopRepo>? = null,
    val showContent: Boolean,
    val hasShownOnboarding: Boolean,
    val token: String,
    val stars: UByte,
    val counter: Byte,
    val reactiveCounterOne: UByte,
    val person: Person?
)

data class UserState(
    val showContent: Boolean,
    val hasShownOnboarding: Boolean,
    val token: String,
    val stars: UByte,
    val counter: Byte,
    val person: Person?
)

@Serializable
data class Person(
    val name: String,
    val age: UByte
)
