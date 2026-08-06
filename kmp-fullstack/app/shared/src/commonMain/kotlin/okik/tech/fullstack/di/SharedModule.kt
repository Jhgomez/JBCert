package okik.tech.fullstack.di

import okik.tech.fullstack.network.ApodApiService
import okik.tech.fullstack.network.NetworkConfig
import okik.tech.fullstack.repository.ApodRepository
import okik.tech.fullstack.repository.ApodRepositoryImpl
import okik.tech.fullstack.ui.ApodViewModel
import org.koin.dsl.module

val sharedModule = module {

    // Network
    single { NetworkConfig.createHttpClient() }

    // API Service
    single {
        ApodApiService(
            httpClient = get(),
            baseUrl = NetworkConfig.DEFAULT_BASE_URL
        )
    }

    // Repository
    single<ApodRepository> { ApodRepositoryImpl(get()) }

    // ViewModel
    single { ApodViewModel(get()) }
}