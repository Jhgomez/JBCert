package okik.tech.jetcert.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import kotlinx.io.files.Path
import okik.tech.jetcert.BuildConfig

class GitHubApi {
    // create your token from your github profile /settings/developer Settings
    private val token: String = BuildConfig.GITHUB_API_KEY

    val apolloClient = ApolloClient.Builder()
        .serverUrl("https://api.github.com/graphql")
        .httpInterceptors(
            listOf(
                AuthInterceptor(token),
                LoggingInterceptor()
            )
        )

        .build()
}