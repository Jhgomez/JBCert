package okik.tech.fullstack.config

import org.koin.dsl.module
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.lang.System
import java.util.*
import kotlin.Int
import kotlin.io.inputStream
import kotlin.io.use
import kotlin.jvm.java
import kotlin.text.take
import kotlin.text.takeLast
import kotlin.text.toBoolean
import kotlin.text.toInt

data class ApodConfig(
    val nasaApiKey: String,
    val initialBatchSize: Int,
    val followupBatchSize: Int,
    val demoInitialBatchSize: Int,
    val demoFollowupBatchSize: Int,
    val batchIntervalHours: Int,
    val demoBatchIntervalHours: Int,
    val maintenanceHour: Int,
    val debugMode: Boolean,
    val debugInitialBatchSize: Int,
    val debugFollowupBatchSize: Int,
    val debugIntervalMinutes: Int,
    val cacheDays: Int
) {
    companion object {
        private val logger = LoggerFactory.getLogger(ApodConfig::class.java)

        fun load(): ApodConfig {
            val props = Properties()

            val configSources = listOf(
                "apod-config.properties",
                "apod-config.dev.properties"
            )

            var configLoaded = false
            for (configFile in configSources) {
                if (loadPropertiesFile(props, configFile)) {
                    logger.info("Loaded configuration from: $configFile")
                    configLoaded = true
                    break
                }
            }

            if (!configLoaded) {
                logger.info("No config file found, using defaults with environment variable overrides")
            }

            return ApodConfig(
                nasaApiKey = getConfigValue(props, "nasa.api.key", "NASA_API_KEY", "DEMO_KEY"),
                initialBatchSize = getConfigValue(props, "batch.initial.size", "INITIAL_BATCH_SIZE", "2").toInt(),
                followupBatchSize = getConfigValue(props, "batch.followup.size", "FOLLOWUP_BATCH_SIZE", "2").toInt(),
                demoInitialBatchSize = getConfigValue(props, "batch.demo.initial.size", "DEMO_INITIAL_BATCH_SIZE", "2").toInt(),
                demoFollowupBatchSize = getConfigValue(props, "batch.demo.followup.size", "DEMO_FOLLOWUP_BATCH_SIZE", "2").toInt(),
                batchIntervalHours = getConfigValue(props, "batch.interval.hours", "BATCH_INTERVAL_HOURS", "1").toInt(),
                demoBatchIntervalHours = getConfigValue(props, "batch.demo.interval.hours", "DEMO_BATCH_INTERVAL_HOURS", "1").toInt(),
                maintenanceHour = getConfigValue(props, "maintenance.hour", "MAINTENANCE_HOUR", "1").toInt(),
                debugMode = getConfigValue(props, "debug.mode", "DEBUG_MODE", "false").toBoolean(),
                debugInitialBatchSize = getConfigValue(props, "debug.initial.batch.size", "DEBUG_INITIAL_BATCH_SIZE", "2").toInt(),
                debugFollowupBatchSize = getConfigValue(props, "debug.followup.batch.size", "DEBUG_FOLLOWUP_BATCH_SIZE", "2").toInt(),
                debugIntervalMinutes = getConfigValue(props, "debug.interval.minutes", "DEBUG_INTERVAL_MINUTES", "2").toInt(),
                cacheDays = getConfigValue(props, "database.cache.days", "CACHE_DAYS", "1").toInt()
            )
        }

        private fun loadPropertiesFile(props: Properties, filename: String): Boolean {
            return try {
                val inputStream: InputStream? = ApodConfig::class.java.classLoader.getResourceAsStream(filename)
                if (inputStream != null) {
                    props.load(inputStream)
                    inputStream.close()
                    true
                } else {
                    val file = File(filename)
                    if (file.exists()) {
                        file.inputStream().use { props.load(it) }
                        true
                    } else {
                        false
                    }
                }
            } catch (e: Exception) {
                logger.warn("Could not load config file $filename: ${e.message}")
                false
            }
        }

        private fun getConfigValue(props: Properties, propKey: String, envKey: String, defaultValue: String): String {
            return System.getenv(envKey)
                ?: props.getProperty(propKey)
                ?: defaultValue
        }
    }

    fun logConfiguration() {
        logger.info("=== APOD Configuration ===")
        logger.info("NASA API Key: ${if (nasaApiKey == "DEMO_KEY") "DEMO_KEY" else "${nasaApiKey.take(4)}...${nasaApiKey.takeLast(4)}"}")
        logger.info("Debug Mode: $debugMode")

        if (debugMode) {
            logger.info("DEBUG SETTINGS:")
            logger.info("  Initial Batch: $debugInitialBatchSize")
            logger.info("  Followup Batch: $debugFollowupBatchSize")
            logger.info("  Interval: $debugIntervalMinutes minutes")
        } else {
            val isDemoKey = nasaApiKey == "DEMO_KEY"
            logger.info("PRODUCTION SETTINGS:")
            logger.info("  Initial Batch: ${if (isDemoKey) demoInitialBatchSize else initialBatchSize}")
            logger.info("  Followup Batch: ${if (isDemoKey) demoFollowupBatchSize else followupBatchSize}")
            logger.info("  Interval: ${if (isDemoKey) demoBatchIntervalHours else batchIntervalHours} hours")
        }

        logger.info("Maintenance Hour: ${maintenanceHour}:00")
        logger.info("Cache Days: $cacheDays")
        logger.info("========================")
    }

    fun getEffectiveInitialBatchSize(): Int = when {
        debugMode -> debugInitialBatchSize
        nasaApiKey == "DEMO_KEY" -> demoInitialBatchSize
        else -> initialBatchSize
    }

    fun getEffectiveFollowupBatchSize(): Int = when {
        debugMode -> debugFollowupBatchSize
        nasaApiKey == "DEMO_KEY" -> demoFollowupBatchSize
        else -> followupBatchSize
    }

    fun getEffectiveBatchIntervalMinutes(): Long = when {
        debugMode -> debugIntervalMinutes.toLong()
        nasaApiKey == "DEMO_KEY" -> demoBatchIntervalHours * 60L
        else -> batchIntervalHours * 60L
    }
}

val apodConfigModule = module { 
    single {
        ApodConfig.load()
    }
}