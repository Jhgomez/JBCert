package okik.tech.jetcert.db

import app.cash.sqldelight.SuspendingTransactionWithoutReturn
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver

class Database(driver: SqlDriver) {

    val newsAdapter = News.Adapter(
        scoreAdapter = IntColumnAdapter,
        descendantsAdapter = IntColumnAdapter
    )

    val repoAdapter = TopRepo.Adapter(
        stargazerCountAdapter = IntColumnAdapter
    )

    val database = JetcertDB(driver, newsAdapter, repoAdapter)
    val newsQueries = database.newsQueries
    val repoQueries = database.topReposQueries

    suspend fun transaction(transaction: suspend SuspendingTransactionWithoutReturn.() -> Unit) {
        database.transaction {
            transaction()
        }
    }
}