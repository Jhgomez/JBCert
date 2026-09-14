package okik.tech.daggerexample

import android.app.Application
import okik.tech.daggerexample.di.AppComponent
import okik.tech.daggerexample.di.DaggerAppComponent

class DaggerDemoApp : Application() {
    val appComponent: AppComponent = DaggerAppComponent.create()
}