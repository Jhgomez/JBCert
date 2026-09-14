package okik.tech.daggerexample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Singleton

/**
 * Main component for the application.
 *
 * See the `TestApplicationComponent` used in UI tests.
 */
@Singleton
@Component(
    modules = [
        SubcomponentsModule::class
    ]
)
interface AppComponent {

}

@Module(
    subcomponents = [
    ]
)
object SubcomponentsModule