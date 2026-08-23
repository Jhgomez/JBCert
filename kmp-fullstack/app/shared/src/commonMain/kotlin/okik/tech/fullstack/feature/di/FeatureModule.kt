package okik.tech.fullstack.feature.di

import okik.tech.fullstack.feature.home.impl.di.homeFeatureModule
import org.koin.dsl.module

val featureModule = module {
    includes(
        homeFeatureModule
    )
}