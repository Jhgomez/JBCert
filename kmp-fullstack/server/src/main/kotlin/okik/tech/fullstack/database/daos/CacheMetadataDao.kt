package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.CacheMetadata
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert
import java.time.LocalDateTime

class CacheMetadataDao {
    // we only store the last time we run a job in case we need to check later
    fun set(type: String, updatedAt: Long) {
        CacheMetadata.upsert(CacheMetadata.id) {
            it[CacheMetadata.id] = type
            it[CacheMetadata.updatedAt] = updatedAt
        }
    }
}

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class CacheMetadataEntity(id: EntityID<String>) : Entity<String>(id) {
    object Dao : EntityClass<String, CacheMetadataEntity>(CacheMetadata)
    var key by CacheMetadata.id
    var value by CacheMetadata.value
    var updatedAt by CacheMetadata.updatedAt


    override fun toString(): String {
        return "CachedMetadataDao(key=$key, value=$value, updatedAt=$updatedAt)"
    }
}