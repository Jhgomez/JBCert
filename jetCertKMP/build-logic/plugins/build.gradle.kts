import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("kmpBuildVariantsPlugin") {
            id = "okik.tech.kmp.buildConfig"
            implementationClass = "KmpBuildVariantsPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.gradle.kmp)
}
