package okik.tech.fullstack.feature.today.impl.di

import okik.tech.fullstack.feature.today.impl.TodayViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val todayFeatureModule = module {
    // ViewModel
    viewModel {
        TodayViewModel(apodRepository = get())
    }
}