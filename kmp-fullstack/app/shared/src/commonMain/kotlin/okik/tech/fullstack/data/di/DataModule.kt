package okik.tech.fullstack.data.di

import okik.tech.fullstack.data.db.di.databaseModule
import okik.tech.fullstack.data.db.di.driverModule
import okik.tech.fullstack.data.network.di.networkModule
import okik.tech.fullstack.data.repository.di.repositoryModule
import org.koin.dsl.module

val dataModule = module {
    includes(
        driverModule,
        databaseModule,
        networkModule,
        repositoryModule
    )
}