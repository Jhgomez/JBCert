package okik.tech.fullstack.data.db

import app.cash.sqldelight.async.coroutines.awaitAsList
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.FullstackDb

class ApodDao(val database: FullstackDb) {
    val queries = database.apodQueries

    suspend fun selectAll(): List<ApodEntity> = queries.selectAll().awaitAsList()

    suspend fun upsert(apods: Array<ApodEntity>): Array<ApodEntity> =
        database.transactionWithResult {
            for (apod in apods) {
                queries.upsert(apod)
            }

            apods
        }
}