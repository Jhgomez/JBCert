package okik.tech.jetcert.di.di

import okik.tech.jetcert.di.initKoin

fun initKoinWeb() {
    initKoin {
        printLogger()
    }
}