package okik.tech.jetcert.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeConfig
import eu.anifantakis.lib.ksafe.KSafeKeyRotationPolicy
import eu.anifantakis.lib.ksafe.awaitCacheReady
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okik.tech.jetcert.db.JetcertDB
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.mp.KoinPlatform.getKoin
import org.w3c.dom.Worker
import kotlin.time.Duration.Companion.days

actual suspend fun koinInit() {
    val sqlDriver = WebWorkerDriver(
        Worker(
            js(
                """new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""
            )
        )
    )

    JetcertDB.Schema.create(sqlDriver).await()

    val platformModule: Module = module {
        single<SqlDriver>(definition = { sqlDriver })

        // Web uses locaStorage, localStorage is per-origin and KSafe already isolates instances via
        // the ksafe.<appNamespace@><fileName>: storage prefix.

        // only use this if your app needs to store not sensitive values, as encryption has a cost
        // Fast, plain writes — for everyday preferences
        single(named("prefs")) {
            KSafe(
                fileName = "prefs",
                config = KSafeConfig(
                    appNamespace = "okik.tech.jetcert.prefs",
                    keyRotationPolicy = KSafeKeyRotationPolicy.MaxAge(2.days),
                )
            )
        }

        // Encrypted writes — for secrets (tokens, passwords, PII)
        single(named("settingsVault")) {
            KSafe(
                fileName = "settingsvault",
                config = KSafeConfig( // config is very important in web and desktop because of the effect the namespace causes
                    appNamespace = "okik.tech.jetcert.vault",
                    keyRotationPolicy = KSafeKeyRotationPolicy.MaxAge(2.days),
                )
            )
        }
        // you can inject these named koin parameters in different ways, you can do that from the
        // module that inject the class to koin or for example in a viewmodel like this(note how names matches)
        // class MyViewModel(
        //     private val prefs: KSafe,  // @Named("prefs") — fast, unencrypted
        //     private val vault: KSafe   // @Named("vault") — encrypted secrets
        // )
    }

    initKoin {
        printLogger()
        modules(platformModule)
    }

    coroutineScope {
        // Kotlin/WASM and Kotlin/JS: WebCrypto encryption is async-only,
        // so KSafe must finish decrypting its cache before your UI reads
        // any encrypted values. So only do this if you are storing ecnrypted values
        launch {
            val ksafe: KSafe = getKoin().get(named("prefs"))
            ksafe.awaitCacheReady()
        }

        launch {
            val ksafe: KSafe = getKoin().get(named("settingsVault"))
            ksafe.awaitCacheReady()
        }
    }
}
