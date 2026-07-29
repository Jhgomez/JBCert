package okik.tech.jetcert

import android.app.Application
import okik.tech.jetcert.di.SetUpKoin
import okik.tech.jetcert.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        SetUpKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}