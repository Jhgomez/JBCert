import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
}

sqldelight {
    databases {
        register("FullstackDb") {
            packageName.set("okik.tech.fullstack.db")
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:2.3.2")
            generateAsync.set(true)
            treatNullAsUnknownForEquality = true
        }
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    android {
       namespace = "okik.tech.fullstack.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.sqldelight.android.driver)
        }
        commonMain.dependencies {
            api(project(":core"))
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.viewmodel)
//            implementation(libs.koin.cmp)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio.engine)
            implementation(libs.ktor.negotiation.client)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging.client)

            implementation(libs.jetbrains.navigation3.ui)
            implementation(libs.jetbrains.material3.adaptiveNavigation3)
            implementation(libs.jetbrains.lifecycle.viewmodelNavigation3)

            implementation(libs.coil.compose)
            implementation(libs.coil.ktor)
            implementation(libs.coil.cache.control)

//            implementation(libs.paging.common)
            implementation(libs.paging.compose)
            implementation(libs.sqldelight.paging3)
            implementation(libs.sqldelight.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        webMain.dependencies {
            implementation(libs.navigation3.browser)
        }
        webMain.dependencies {
            implementation(libs.sqldelight.web.worker)
            implementation(
                devNpm(libs.plugins.webpack.plugin.get().pluginId,
                    libs.versions.webpack.plugin.get())
            )
            implementation(
                npm(
                    libs.plugins.sql.js.get().pluginId,
                    libs.versions.sql.js.get())
            )
            implementation(
                npm(
                    libs.plugins.sqljs.worker.get().pluginId,
                    libs.versions.sqldelight.get())
            )
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.jvm.driver)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}