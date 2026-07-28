package okik.tech.jetcert.api.clients

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okik.tech.jetcert.BuildConfig
import okik.tech.jetcert.api.AuthInterceptor

val ktorClient: HttpClient = HttpClient {
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

private const val TOKEN: String = BuildConfig.GITHUB_API_KEY

val apolloClient: ApolloClient = ApolloClient.Builder()
    .serverUrl("https://api.github.com/graphql")
    .httpInterceptors(
        listOf(
            AuthInterceptor(TOKEN),
            LoggingInterceptor()
        )
    ).build()