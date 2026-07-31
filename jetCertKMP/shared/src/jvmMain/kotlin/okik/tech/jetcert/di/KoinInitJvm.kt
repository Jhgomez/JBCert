package okik.tech.jetcert.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.apollographql.apollo.ApolloClient
import eu.anifantakis.lib.ksafe.KSafe
import okik.tech.jetcert.api.clients.apolloClient
import okik.tech.jetcert.db.JetcertDB
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.dsl.module

suspend fun initKoinJvm() {
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    // this is leveraging the generateAsync = true gradlew setting and lets us use an asynchronous schema
    JetcertDB.Schema.create(driver).await()

    val jvmModule: Module = module {
        single<SqlDriver> (definition = { driver })

        single(named("prefs")) {
            KSafe(fileName = "prefs")
        }

        // Encrypted writes — for secrets (tokens, passwords, PII)
        single(named("settingsVault")) {
            KSafe(fileName = "settingsvault")
        }
    }

    initKoin {
        modules(jvmModule)
    }
}