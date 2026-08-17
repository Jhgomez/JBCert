package okik.tech.fullstack.di

import okik.tech.fullstack.data.network.di.networkModule
import okik.tech.fullstack.data.repository.di.repositoryModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            networkModule,
            repositoryModule
        )
    }
}