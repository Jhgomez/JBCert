package okik.tech.daggerexample.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import okik.tech.daggerexample.client.di.ClientModule
import okik.tech.daggerexample.data.repository.di.RepoAModule
import okik.tech.daggerexample.data.service.di.ServiceAModule
import javax.inject.Singleton

/**
 * Main component for the application.
 *
 * See the `TestApplicationComponent` used in UI tests.
 */
@Singleton
@Component(
    modules = [
        ClientModule::class,
        ServiceAModule::class,
        RepoAModule::class,
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