package okik.tech.jetcert.api

import com.apollographql.apollo.ApolloClient
import kotlinx.io.files.Path

class GitHubApi {
    // create your token from your github profile /settings/developer Settings
    private val token: String
    init {
        token = ""
    }

    val apolloClient = ApolloClient.Builder()
        .serverUrl("https://example.com/graphql")
        .addHttpInterceptor(AuthInterceptor(token))
        .build()
}