package okik.tech.fullstack.data.network.di

import okik.tech.fullstack.data.network.client.NetworkConfig
import okik.tech.fullstack.data.network.restapi.services.ApodApiService
import org.koin.dsl.module

val networkModule = module {

    // Network
    single { NetworkConfig.createHttpClient() }

    // API Service
    single { ApodApiService(httpClient = get()) }
}