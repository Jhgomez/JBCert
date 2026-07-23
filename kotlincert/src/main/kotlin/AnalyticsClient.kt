package okik.tech

interface AnalyticsClient {
    fun track(event: String )
}