package okik.tech.fullstack.data.db.di

import app.cash.sqldelight.db.SqlDriver
import okik.tech.fullstack.data.db.AndroidDbDriverFactoryImpl
import okik.tech.fullstack.data.db.DbDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val driverModule: Module = module {
    single<SqlDriver> { AndroidDbDriverFactoryImpl(context = androidContext()).synchronousGet() }
}