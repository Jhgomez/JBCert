package okik.tech.jetcert.di

import com.apollographql.apollo.ApolloClient
import io.ktor.client.HttpClient
import okik.tech.jetcert.MainViewModel
import okik.tech.jetcert.api.NewsApi
import okik.tech.jetcert.api.clients.apolloClient
import okik.tech.jetcert.api.clients.ktorClient
import okik.tech.jetcert.db.Database
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::ktorClient) bind HttpClient::class // clasic DSL
    single { apolloClient } bind ApolloClient::class    // classic DSL

    single<ApolloClient>(definition = { apolloClient })  // compiler DSL
    singleOf(::NewsApi)


//    single<Database>() // compiler plugign DSL
    singleOf(::Database) // classic DSL

////    viewModel<MainViewModel>()
//    viewModelOf(::MainViewModel) // classic DSL

    viewModel {  // also classic DSL
        MainViewModel(get(), get(), get(named("settingsVault")))
    }
}
