package okik.tech.fullstack.feature.di

import okik.tech.fullstack.feature.home.impl.ApodList.HomeListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureModule = module {
    viewModel {
        HomeListViewModel(apodRepository = get())
    }
}