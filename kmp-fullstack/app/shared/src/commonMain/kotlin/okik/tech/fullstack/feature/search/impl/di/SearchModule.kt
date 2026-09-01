package okik.tech.fullstack.feature.search.impl.di

import okik.tech.fullstack.feature.search.impl.SearchViewModel
import okik.tech.fullstack.feature.today.impl.TodayViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchFeatureModule = module {
    // ViewModel
    viewModel {
        SearchViewModel(apodRepository = get())
    }
}