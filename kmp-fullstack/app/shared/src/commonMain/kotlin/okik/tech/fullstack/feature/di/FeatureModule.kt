package okik.tech.fullstack.feature.di

import okik.tech.fullstack.feature.home.impl.di.homeFeatureModule
import okik.tech.fullstack.feature.today.impl.di.todayFeatureModule
import org.koin.dsl.module

val featureModule = module {
    includes(
        homeFeatureModule,
        todayFeatureModule
    )
}