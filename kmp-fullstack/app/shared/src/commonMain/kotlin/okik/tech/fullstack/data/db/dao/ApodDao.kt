package okik.tech.fullstack.data.db.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.paging3.QueryPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import okik.tech.fullstack.db.ApodEntity
import okik.tech.fullstack.db.FullstackDb

class ApodDaoImpl(val database: FullstackDb, val dispatcher: CoroutineDispatcher) : ApodDao {
    private val queries = database.apodQueries

    override val source = QueryPagingSource(
        countQuery = queries.countApods(),
        transacter = queries,
        context = dispatcher,
        queryProvider = queries::apodsPage
    )

    override suspend fun selectAll(): List<ApodEntity> = queries.selectAll().awaitAsList()

    override suspend fun upsertApods(apods: Array<ApodEntity>): Array<ApodEntity> {
        for (apod in apods) {
            queries.upsert(apod)
        }

        return apods
    }

    override suspend fun upsertApod(apod: ApodEntity): ApodEntity {
        queries.upsert(apod)

        return apod
    }

    override suspend fun deleteAll() {
        queries.deleteAll()
    }
}