package okik.tech.jetcert.di

import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import okik.tech.jetcert.MainViewModel
import okik.tech.jetcert.api.NewsApi
import okik.tech.jetcert.api.clients.apolloClient
import okik.tech.jetcert.api.clients.ktorClient
import okik.tech.jetcert.db.Database
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val sharedModule = module {
    single<Database>() // compiler plugign DSL
    singleOf(::ktorClient) bind HttpClient::class // clasic DSL
    single { apolloClient } bind ApolloClient::class
    single<NewsApi>()
    viewModel<MainViewModel>()
}

// Platform-specific modules (defined per platform)
expect val platformModule: Module