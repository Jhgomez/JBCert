package okik.tech.jetcert.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsApi : KoinComponent {
    // Constructor injection may be preferred but just to showcase field injection
    val client: HttpClient by inject()

    suspend fun getTopStories(): List<NewsResponse.Story> {
        val storyIds: List<String> = client.get("https://hacker-news.firebaseio.com/v0/topstories.json").body()
        return storyIds.take(10).mapNotNull {
            val story: NewsResponse = client.get("https://hacker-news.firebaseio.com/v0/item/$it.json").body()
            story as? NewsResponse.Story
        }
    }
}