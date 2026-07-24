import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.sources.android.androidSourceSetInfo
import java.io.File
import java.util.Properties

/*
    To use this plugin add it in a module that is applying kotlin multiplatform, most likely you'd like
    to apply it to a common module(shared module) so you can call it from any platform, this was registered
    in our build logic as "okik.tech.kmp.buildConfig", then in the gradle build script you applied it
    call the buildConfig DSL, you need to set the package name so that the generated buildconfig.kt file
    can have a correct package name, and then add any property, you can add it to an environment variable or
    in the local.properties file, after you do that call buildConfigField("YOUR_KEY_NAME") in the
    buildConfig DSL block, you can add properties directly like stringField(name, val), booleanField(name, val),
    intField(name, val), this will give them a type instead to fall back to Strings

    buildConfig {
        packageName = "com.example"
        buildConfigField("APY_KEY")
        booleanField("SHOULD_SHOW_PRODUCTION_SCREEN", false)
    }
 */
class KmpBuildVariantsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("buildConfig", BuildConfigExtension::class.java, target)

        target.plugins.withId("org.jetbrains.kotlin.multiplatform") {
            val kotlin = target.extensions.getByType<KotlinMultiplatformExtension>()

            val generateTask = target.tasks.register("generateBuildConfig", GenerateBuildConfigTask::class.java) {
                packageName.set(extension.packageName)
                stringFields.set(extension.stringFields)
                booleanFields.set(extension.booleanFields)
                intFields.set(extension.intFields)
                outputDir.set(target.layout.buildDirectory.dir("generated/buildconfig/commonMain"))
            }

            kotlin.sourceSets.getByName("commonMain") {
                this.kotlin.srcDir(generateTask.map { it.outputDir })
            }

            target.tasks.named("compileKotlinMetadata") {
                dependsOn(generateTask)
            }
        }
    }
}

public abstract class BuildConfigExtension(private val project: Project) {

    abstract val packageName: Property<String>
    internal abstract val stringFields: MapProperty<String, String>
    internal abstract val booleanFields: MapProperty<String, Boolean>
    internal abstract val intFields: MapProperty<String, Int>

    private val localProperties: Properties by lazy {
        val props = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { props.load(it) }
        }
        props
    }

    public fun stringField(name: String, value: String) {
        stringFields.put(name, value)
    }

    public fun booleanField(name: String, value: Boolean) {
        booleanFields.put(name, value)
    }

    public fun intField(name: String, value: Int) {
        intFields.put(name, value)
    }

    public fun buildConfigField(name: String) {
        val value = localProperties.getProperty(name) ?: System.getenv(name)
        requireNotNull(value) { "$name not found in local.properties or environment variables" }

        stringFields.put(name, value)
    }
}

abstract class GenerateBuildConfigTask : DefaultTask() {

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val intFields: MapProperty<String, Int>
    @get:Input
    public abstract val stringFields: MapProperty<String, String>
    @get:Input
    public abstract val booleanFields: MapProperty<String, Boolean>

    @get:OutputDirectory
    public abstract val outputDir: DirectoryProperty

    @TaskAction
    public fun generate() {
        val packageNameValue = packageName.get()
        val outputDirectory = outputDir.get().asFile

        outputDirectory.deleteRecursively()
        outputDirectory.mkdirs()

        val fileContent = buildString {
            appendLine("package $packageNameValue")
            appendLine()
            appendLine("public object BuildConfig {")

            // Generate string fields
            stringFields.get().forEach { (name, value) ->
                appendLine("    public const val $name: String = \"$value\"")
            }

            // Generate boolean fields
            booleanFields.get().forEach { (name, value) ->
                appendLine("    public const val $name: Boolean = $value")
            }

            // Generate int fields
            intFields.get().forEach { (name, value) ->
                appendLine("    public const val $name: Int = $value")
            }

            appendLine("}")
        }

        val packagePath = packageNameValue.replace('.', '/')
        val targetDir = File(outputDirectory, packagePath)
        targetDir.mkdirs()

        File(targetDir, "BuildConfig.kt").writeText(fileContent)
    }
}