package okik.tech.daggerexample

import android.app.Application
import okik.tech.daggerexample.di.AppComponent
import okik.tech.daggerexample.di.DaggerAppComponent

class DaggerDemoApp : Application() {
    private var appComponent: AppComponent? = null

    fun getAppComponent(): AppComponent = if (appComponent == null) {
        DaggerAppComponent.create().also {
            appComponent = it
        }
    } else {
        appComponent!!
    }
}