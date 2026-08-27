package okik.tech.fullstack.config

import okik.tech.fullstack.services.ApodService
import okik.tech.fullstack.services.NasaApiClient
import org.koin.dsl.module

val serviceModule = module {
    single {
        val config = get<AppConfig>()

        NasaApiClient(
            apiKey = config.nasaApiKey,
            cacheDirectory = config.cacheDirectory,
            cacheHdDirectory = config.cacheHdDirectory,
            cacheTempDirectory = config.cacheTempDirectory
        )
    }

    single {
        ApodService(
            nasaApiClient = get(),
            apodDao = get(),
            cacheMetadataDao = get(),
            mediaDao = get(),
            mediaHdDao = get(),
            cacheDays = get<AppConfig>().cacheDays
        )
    }
}