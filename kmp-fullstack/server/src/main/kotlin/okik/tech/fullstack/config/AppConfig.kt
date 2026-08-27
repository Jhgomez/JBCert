package okik.tech.fullstack.config

import org.koin.dsl.module

data class AppConfig(
    val nasaApiKey: String = System.getenv("NASA_API_KEY") ?: "DEMO_KEY",
    val cacheDays: Int = System.getenv("CACHE_DAYS")?.toIntOrNull() ?: 90, // how many days we will hold data before cleaning some space
    val dbFilePath: String = "generated-db/apod.db",
    val cacheDirectory: String = "~/JBCert/kmp-fullstack/cachefiles",
    val cacheHdDirectory: String = "~/JBCert/kmp-fullstack/cachehdfiles",
    val cacheTempDirectory: String = "~/JBCert/kmp-fullstack/cachetemp"
)

val appConfigModule = module {
    single { AppConfig() }
}
