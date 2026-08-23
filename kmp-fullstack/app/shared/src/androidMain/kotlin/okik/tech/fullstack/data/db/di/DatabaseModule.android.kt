package okik.tech.fullstack.data.db.di

import okik.tech.fullstack.data.db.AndroidDbDriverProducerImpl
import okik.tech.fullstack.data.db.DbDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single<DbDriverFactory> { AndroidDbDriverProducerImpl(context = androidContext()) }
}