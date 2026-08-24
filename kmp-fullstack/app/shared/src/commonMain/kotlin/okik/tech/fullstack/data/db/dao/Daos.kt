package okik.tech.fullstack.data.db.dao

import androidx.paging.PagingSource

interface ApodDao<Entity: Any> {
    val source: PagingSource<Int, Entity>

    suspend fun selectAll(): List<Entity>
    suspend fun upsertApods(apods: Array<Entity>): Array<Entity>
    suspend fun upsertApod(apod: Entity): Entity
}

interface PagingInfoDao<Entity: Any> {
    suspend fun select(id: String): Entity
    suspend fun upsertPage(entry: Entity): Entity
    suspend fun delete()
}

