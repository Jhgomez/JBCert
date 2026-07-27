package okik.tech.jetcert.di

import okik.tech.jetcert.db.AndroidDatabaseDriverFactory
import okik.tech.jetcert.db.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule = module {
    // Classic DSL with lambda for custom construction
//    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
    single<AndroidDatabaseDriverFactory>() bind DatabaseDriverFactory::class
}