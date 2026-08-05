package okik.tech.fullstack.config

import okik.tech.fullstack.services.ApodService
import okik.tech.fullstack.services.NasaApiClient
import org.koin.dsl.module

val serviceModule = module {
    single {
        val config = get<AppConfig>()

        NasaApiClient(apiKey = config.nasaApiKey)
    }

    single {
        ApodService(
            nasaApiClient = get(),
            apodDao = get(),
            cacheMetadataDao = get(),
            cacheDays = get<AppConfig>().cacheDays
        )
    }
}