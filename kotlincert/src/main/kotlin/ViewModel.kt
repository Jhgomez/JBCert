package okik.tech

import okik.tech.ViewModel.Single.doSomething

class ViewModel constructor(private val analyticsClient: AnalyticsClient) {

    fun submitClick() {
        analyticsClient.track("click")

        doSomething()

    }

    object Single {
        const val MAX = 100

        fun doSomething() {}
    }
}