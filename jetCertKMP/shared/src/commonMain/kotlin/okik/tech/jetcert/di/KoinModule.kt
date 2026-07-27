package okik.tech.jetcert.di

import okik.tech.jetcert.MainViewModel
import okik.tech.jetcert.db.Database
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val sharedModule = module {
    single<Database>()
    viewModel<MainViewModel>()
}

// Platform-specific modules (defined per platform)
expect val platformModule: Module