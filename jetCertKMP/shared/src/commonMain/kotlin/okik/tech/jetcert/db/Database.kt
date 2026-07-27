package okik.tech.jetcert.db

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = JetcertDB(databaseDriverFactory.createDriver())
    private val newsQueries = database.newsQueries
    private val repoQueries = database.topReposQueries
}