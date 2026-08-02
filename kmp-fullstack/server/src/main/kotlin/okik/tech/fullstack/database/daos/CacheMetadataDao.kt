package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.CacheMetadata
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class CacheMetadataDao(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, CacheMetadataDao>(CacheMetadata)

    var key by CacheMetadata.key
    var value by CacheMetadata.value
    var updatedAt by CacheMetadata.updatedAt


    override fun toString(): String {
        return "CachedMetadataDao(key=$key, value=$value, updatedAt=$updatedAt)"
    }

    suspend fun set(type: String, currentMillis: String) {
//        "daily_maintenance_last_run"
    }
}