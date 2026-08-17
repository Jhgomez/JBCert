package okik.tech.fullstack.feature.home.impl.di

import okik.tech.fullstack.feature.home.impl.ApodList.HomeLIstViewModel
import okik.tech.fullstack.ui.ApodViewModel
import org.koin.dsl.module

val homeFeatureModule = module {
    // ViewModel
    single { HomeLIstViewModel(get()) }
}