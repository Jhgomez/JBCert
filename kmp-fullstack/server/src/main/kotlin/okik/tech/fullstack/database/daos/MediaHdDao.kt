package okik.tech.fullstack.database.daos

import okik.tech.fullstack.database.tables.Media
import okik.tech.fullstack.database.tables.MediaHd
import okik.tech.fullstack.services.FileInfo
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.statements.api.ExposedBlob
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.upsert

class MediaHdDao {
    fun set(fileInfo: FileInfo): String {
        Media.upsert(Media.id) {
            it[Media.id] = fileInfo.url
            it[Media.mediaFile] = fileInfo.fileName
            it[Media.contentType] = fileInfo.contentType
        }

        return fileInfo.fileName
    }

    fun get(keyUrl: String): String? {
        return MediaHdEntity.Dao.findById(keyUrl)?.pictureFile
    }
}

/**
 *  using the DAO pattern https://www.jetbrains.com/help/exposed/get-started-with-exposed-dao.html
 *  however using this new DSL may not be that great since doesn't have the same capability as the
 *  classic DSL, for example it doesn't support "upsert"
 */
class MediaHdEntity(id: EntityID<String>) : Entity<String>(id) {
    object Dao : EntityClass<String, MediaHdEntity>(MediaHd)
    var key by MediaHd.id
    var pictureFile by MediaHd.pictureFile
    var contentType by MediaHd.contentType

    override fun toString(): String {
        return "MediaHdDao(key=$key, pictureFile=${pictureFile}, contentType=$contentType)"
    }
}