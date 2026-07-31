package okik.tech.jetcert.di

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import eu.anifantakis.lib.ksafe.KSafe
import okik.tech.jetcert.db.JetcertDB
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module


fun SetUpKoin(config: KoinAppDeclaration? = null) {
    fun getDriver(context: Context): SqlDriver = AndroidSqliteDriver(
        JetcertDB.Schema.synchronous(),
        context,
        "Jetcert.db"
    )

    val androidKoinModule = module {
        // single<SqlDriver>(definition = { getDriver(get()) })  compiler DSL
        // single { getDriver(get()) } bind SqlDriver::get       Classic DSL

        single { getDriver(get()) } // Classic DSL

        // only use this if your app needs to store not sensitive values, as encryption has a cost
        // Fast, plain writes — for everyday preferences
        single(named("prefs")) {
            KSafe(
                context = androidApplication(), // get<Context> or just get()
                fileName = "prefs"
            )
        }

        // Encrypted writes — for secrets (tokens, passwords, PII)
        single(named("vault")) {
            KSafe(
                context = get<Context>(),
                fileName = "vault"
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
        includes(config)
        modules(androidKoinModule)
    }
}