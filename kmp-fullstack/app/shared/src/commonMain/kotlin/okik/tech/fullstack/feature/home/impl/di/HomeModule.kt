package okik.tech.fullstack.feature.home.impl.di

import okik.tech.fullstack.feature.home.impl.ApodList.HomeListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeFeatureModule = module {
    // ViewModel
    viewModel {
        HomeListViewModel(apodRepository = get())
    }
}