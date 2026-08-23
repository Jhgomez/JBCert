package okik.tech.fullstack.di

import okik.tech.fullstack.data.db.di.getDriverModule
import org.koin.core.module.Module
import org.koin.dsl.includes

suspend fun WebMainInitKoin() {
    val sqlDriverModule = getDriverModule()

    initKoin {
        modules(sqlDriverModule)
    }
}