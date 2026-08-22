package okik.tech.fullstack

import android.app.Application
import okik.tech.fullstack.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FullstackApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            // Reference Android context
            androidContext(this@FullstackApp)
        }
    }
}