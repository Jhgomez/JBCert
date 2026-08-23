package okik.tech.fullstack.di

import okik.tech.fullstack.data.di.dataModule
import okik.tech.fullstack.data.network.di.networkModule
import okik.tech.fullstack.data.repository.di.repositoryModule
import okik.tech.fullstack.feature.di.featureModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(
            dataModule,
            featureModule
        )
    }
}