package okik.tech.jetcert.di

import okik.tech.jetcert.MainViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val sharedModule = module {
    viewModel<MainViewModel>()
}

// Platform-specific modules (defined per platform)
expect val platformModule: Module