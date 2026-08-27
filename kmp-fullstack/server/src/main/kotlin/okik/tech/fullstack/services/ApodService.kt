package okik.tech.fullstack.services

import kotlinx.coroutines.delay
import okik.tech.fullstack.database.daos.ApodDao
import okik.tech.fullstack.database.daos.CacheMetadataDao
import okik.tech.fullstack.database.daos.MediaDao
import okik.tech.fullstack.database.daos.MediaHdDao
import okik.tech.fullstack.models.ApodResponse
import okik.tech.fullstack.models.PaginatedResponse
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeParseException

// I would actually call this a "repository" in an client Android app, a service in an Android client
// would actually be the one making http calls using a rest client, that service would then be
class ApodService(
    private val nasaApiClient: NasaApiClient,
    private val apodDao: ApodDao,
    private val cacheMetadataDao: CacheMetadataDao,
    private val mediaDao: MediaDao,
    private val mediaHdDao: MediaHdDao,
    private val cacheDays: Int
) {
    private val logger = LoggerFactory.getLogger(ApodService::class.java)

    suspend fun getTodayApod(): ApodResponse {
        val today = LocalDate.now()
        val cachedApod = apodDao.getByDate(today)

        if (cachedApod != null) {
            logger.info("Returning today's APOD from cache")
            return cachedApod
        }

        try {
            logger.info("Fetching today's APOD from NASA API")
            val apod = nasaApiClient.getTodayApod()
            apodDao.save(apod)
            return apod
        } catch (e: NasaApiException) {
            logger.warn("Rate limit hit when fetching today's APOD. Trying yesterday's as fallback.")
            val yesterday = LocalDate.now().minusDays(1)
            val yesterdayApod = apodDao.getByDate(yesterday)

            if (yesterdayApod != null) {
                return yesterdayApod.copy(
                    title = "[FALLBACK] ${yesterdayApod.title}",
                    explanation = "NASA API rate limit reached. Showing yesterday's image as a fallback.\n\n${yesterdayApod.explanation}"
                )
            }

            throw e
        }
    }

    suspend fun getApodByDate(date: LocalDate): ApodResponse {
        validateDate(date)
        val cachedApod = apodDao.getByDate(date)

        return if (cachedApod != null) {
            logger.info("Returning APOD for $date from cache")
            cachedApod
        } else {
            logger.info("Fetching APOD for $date from NASA API")
            val apod = nasaApiClient.getApodByDate(date.toString())
            apodDao.save(apod)
            apod
        }
    }

    suspend fun getRandomApod(): ApodResponse {
        val randomFromCache = apodDao.getRandom()

        return if (randomFromCache != null) {
            logger.info("Returning random APOD from cache")
            randomFromCache
        } else {
            logger.info("Fetching random APOD from NASA API")
            val apod = nasaApiClient.getRandomApod()
            apodDao.save(apod)
            apod
        }
    }

    suspend fun getApodHistory(
        page: UByte,
        pageSize: UByte,
        start: LocalDate?,
        end: LocalDate?
    ): PaginatedResponse<ApodResponse> {
        require(page > 0U) { "Page must be greater than 0" }
        require(pageSize > 0U) { "Page size must be greater than 0" }
        require(pageSize <= 100U) { "Page size cannot exceed 100" }

        if (start != null && end != null) {
            validateDate(start)
            validateDate(end)
        }

        val (items, totalCount) = apodDao.getPaginated(page, pageSize, start, end)

        if (items.isEmpty() && totalCount == 0.toUShort()) {
            val today = LocalDate.now()
            var startDate = today.minusDays(minOf(30, cacheDays.toLong()))

            var endDate = today

            if (start != null && end != null) {
                val offset = pageSize * (page - 1U)

                startDate = start.plusDays(offset.toLong())

                if (startDate.isAfter(today)) {
                    throw IllegalStateException("Page can not start in the future")
                }

                val offsetEndDate = startDate.plusDays(pageSize.toLong())

                if (offsetEndDate.isAfter(end)) {
                    endDate = end
                }
            }

            fillHistoricalCache(
                startDate,
                endDate
            )

            val (newItems, newTotalCount) = apodDao.getPaginated(page, pageSize, startDate, endDate)

            return PaginatedResponse(
                items = newItems,
                page = page,
                pageSize = pageSize,
                totalItems = newTotalCount,
                totalPages = calculateTotalPages(newTotalCount, pageSize)
            )
        }

        return PaginatedResponse(
            items = items,
            page = page,
            pageSize = pageSize,
            totalItems = totalCount,
            totalPages = calculateTotalPages(totalCount, pageSize)
        )
    }

    suspend fun fillHistoricalCache(startDate: LocalDate, endDate: LocalDate): Int {
        require(!startDate.isAfter(endDate)) { "Start date cannot be after end date" }

        logger.info("Filling historical cache from $startDate to $endDate")

        var currentDate = startDate
        var count = 0

        while (!currentDate.isAfter(endDate)) {
            try {
                if (apodDao.getByDate(currentDate) == null) {
                    try {
                        val apod = nasaApiClient.getApodByDate(currentDate.toString())
                        apodDao.save(apod)
                        count++
                        delay(600)
                    } catch (e: NasaApiException) {
                        if (e.message?.contains("Rate limit") == true) {
                            logger.warn("Rate limit hit while filling cache. Pausing for 5 minutes.")
                            delay(5 * 60 * 1000)
                        } else {
                            throw e
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("fillHistoricalCache Error fetching APOD for $currentDate: ${e.message}")
            }

            currentDate = currentDate.plusDays(1)
        }

        logger.info("Added $count new entries to cache")
        return count
    }

    /**
     * Cleans cache and fills it up every single day
     */
    suspend fun runDailyCacheMaintenanceJob() {
        logger.info("Running daily cache maintenance job")

        try {
            getTodayApod()

            val deletedCount = cleanupOldEntries(cacheDays)
            logger.info("Removed $deletedCount old entries from cache")

            val today = LocalDate.now()
            val oldestDate = today.minusDays(cacheDays.toLong())
            val addedCount = fillMissingEntries(oldestDate, today)
            logger.info("Added $addedCount missing entries to cache")

            cacheMetadataDao.set("daily_maintenance_last_run", Instant.now().toEpochMilli())

            logger.info("Daily cache maintenance completed successfully")
        } catch (e: Exception) {
            logger.error("Error in daily cache maintenance: ${e.message}", e)
        }
    }

    private suspend fun fillMissingEntries(startDate: LocalDate, endDate: LocalDate): Int {
        var currentDate = startDate
        var count = 0

        while (!currentDate.isAfter(endDate)) {
            try {
                if (apodDao.getByDate(currentDate) == null) {
                    val apod = nasaApiClient.getApodByDate(currentDate.toString())
                    apodDao.save(apod)
                    count++
                }
            } catch (e: Exception) {
                logger.error("fillMissingEntries Error fetching APOD for $currentDate: ${e.message}")
            }

            currentDate = currentDate.plusDays(1)
        }

        return count
    }

    private suspend fun cleanupOldEntries(keepDays: Int): Int {
        val cutoffDate = LocalDate.now().minusDays(keepDays.toLong())
        return apodDao.deleteOlderThan(cutoffDate)
    }

    private fun validateDate(date: LocalDate): LocalDate {
        return try {

            if (date.isAfter(LocalDate.now())) {
                throw IllegalStateException("Date can not be in the future")
            }

            if (date.isBefore(LocalDate.of(1996, Month.JUNE, 16))) {
                throw IllegalStateException("Date can not be before 1996-06-16.")
            }

            date
        } catch (_: DateTimeParseException) {
            throw IllegalArgumentException("Invalid date format. Use YYYY-MM-DD format.")
        }
    }

    private fun calculateTotalPages(totalItems: UShort, pageSize: UByte): UByte {
        return if (totalItems == 0.toUShort()) 1U else (totalItems/pageSize + 1U).toUByte()
    }

    suspend fun needsHistoricalDataFetch(): Boolean {
        try {
            val totalCount = apodDao.getTotalCount()

            if (totalCount < 90) {
                logger.info("Database has only $totalCount entries. Historical fetch needed.")
                return true
            }

            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5)
            val recentCount = apodDao.countInDateRange(fiveDaysAgo, today)

            if (recentCount < 5) {
                logger.info("Database has only $recentCount recent entries. Historical fetch needed.")
                return true
            }

            logger.info("Database appears adequately populated ($totalCount total, $recentCount recent)")
            return false

        } catch (e: Exception) {
            logger.error("Error checking database status", e)
            return true
        }
    }

    suspend fun getMedia(url: String): FileInfo {
        val resourceBytes = mediaDao.get(url)

        if (resourceBytes == null) {
            val fileInfo = nasaApiClient.getMedia(url)
            mediaDao.set(fileInfo)

            return fileInfo
        }

        return resourceBytes
    }

    suspend fun getMediaHd(url: String): FileInfo {
        val resourceBytes = mediaHdDao.get(url)

        if (resourceBytes == null) {
            val fileInfo = nasaApiClient.getMediaHd(url)
            mediaHdDao.set(fileInfo)

            return fileInfo
        }

        return resourceBytes
    }
}