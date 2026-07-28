package okik.tech.jetcert.db

import app.cash.sqldelight.SuspendingTransactionWithoutReturn
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter

class Database(databaseDriverFactory: DatabaseDriverFactory) {

    val newsAdapter = News.Adapter(
        scoreAdapter = IntColumnAdapter,
        descendantsAdapter = IntColumnAdapter
    )

    val repoAdapter = TopRepo.Adapter(
        stargazerCountAdapter = IntColumnAdapter
    )

    val database = JetcertDB(databaseDriverFactory.createDriver(), newsAdapter, repoAdapter)
    val newsQueries = database.newsQueries
    val repoQueries = database.topReposQueries

    suspend fun transaction(transaction: suspend SuspendingTransactionWithoutReturn.() -> Unit) {
        database.transaction {
            transaction()
        }
    }
}