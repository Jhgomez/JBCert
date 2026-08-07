package okik.tech.fullstack

import android.app.Application
import okik.tech.fullstack.di.initKoin

class FullstackApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}