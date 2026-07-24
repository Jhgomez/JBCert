package okik.tech.jetcert.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NewsApi {
    // should be using Dependency injection here
    val client = HttpClient {
        // this gives the capability to handle json by installing the content negotiation module with the below json block
        install(ContentNegotiation) {
            json(
                Json {
                    encodeDefaults = true
                    isLenient = true    // doesn't enforce serialization classes to deserealize all properties of a json with its properties, you can handle some properties only
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }
    }

    suspend fun getTopStories(): List<NewsResponse.Story> {
        val storyIds: List<String> = client.get("https://hacker-news.firebaseio.com/v0/topstories.json").body()
        return storyIds.take(10).mapNotNull {
            val story: NewsResponse = client.get("https://hacker-news.firebaseio.com/v0/item/$it.json").body()
            story as? NewsResponse.Story
        }
    }
}