package okik.tech.fullstack.data.db.di

import okik.tech.fullstack.data.db.DbDriverFactory
import okik.tech.fullstack.data.db.IosDbDriverFactoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single<DbDriverFactory> { IosDbDriverFactoryImpl() }
}