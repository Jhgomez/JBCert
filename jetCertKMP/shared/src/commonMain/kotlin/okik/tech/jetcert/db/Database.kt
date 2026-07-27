package okik.tech.jetcert.db

import app.cash.sqldelight.adapter.primitive.IntColumnAdapter

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    val newsAdapter = News.Adapter(
        scoreAdapter = IntColumnAdapter,
        descendantsAdapter = IntColumnAdapter
    )

    val repoAdapter = TopRepo.Adapter(
        stargazerCountAdapter = IntColumnAdapter
    )

    private val database = JetcertDB(databaseDriverFactory.createDriver(), newsAdapter, repoAdapter)
    private val newsQueries = database.newsQueries
    private val repoQueries = database.topReposQueries
}