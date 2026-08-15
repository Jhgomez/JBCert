package okik.tech.fullstack.feature.about.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import okik.tech.fullstack.feature.about.api.AboutHome

fun EntryProviderScope<NavKey>.aboutEntry() {
    entry<AboutHome> {
        AboutScreen()
    }
}