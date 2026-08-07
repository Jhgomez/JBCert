package okik.tech.fullstack.di

import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import okik.tech.fullstack.data.network.client.NetworkConfig
import okik.tech.fullstack.data.repository.ApodRepositoryImpl
import okik.tech.fullstack.domain.ApodRepository
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