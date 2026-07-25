package okik.tech.jetcert

import android.app.Application
import android.content.Context

var appContext: Context? = null

object AndroidApp: Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }
}