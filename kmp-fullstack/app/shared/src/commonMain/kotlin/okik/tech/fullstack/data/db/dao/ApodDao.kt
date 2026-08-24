package okik.tech.fullstack.data.db.dao

import androidx.paging.PagingSource
import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.paging3.QueryPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.FullstackDb

class ApodDao(val database: FullstackDb, val dispatcher: CoroutineDispatcher) {
    val queries = database.apodQueries

    val source = QueryPagingSource(
        countQuery = queries.countApods(),
        transacter = queries,
        context = dispatcher,
        queryProvider = queries::apodsPage
    )

    suspend fun selectAll(): List<ApodEntity> = queries.selectAll().awaitAsList()

    suspend fun upsertApods(apods: Array<ApodEntity>): Array<ApodEntity> =
        database.transactionWithResult {
            for (apod in apods) {
                queries.upsert(apod)
            }

            apods
        }

    suspend fun upsertApod(apod: ApodEntity): ApodEntity {
        queries.upsert(apod)

        return apod
    }
}