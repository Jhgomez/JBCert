import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization) // found in kmp docs when adding kton
    alias(libs.plugins.apollo)
    alias(libs.plugins.okik.tech.buildConfig)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.koin.compiler)
}

sqldelight {
    databases {
        // we could use create or register, create generates the DB eagerly while register does it lazily
        // which helps us decrease build times becase it avoids running the the entire block
        // if the db task is not called as opposed to run it on evey build, the name you register here
        // is the name of your DB. After you sync you can explicitly generate the kotlin APIs that will
        // let you interact with your DB using `./gradlew generateCommonMainJetcertDBInterface` not this
        // command includes the registered name of the the DB
        register("JetcertDB") {
            // this package name has to mimic the directory struture where you will declare your
            // tables/db schemas
            packageName.set("okik.tech.jetcert.db")
            // if not set it defaults to an "old" version(3.18) of sqlite dialect, you can use
            // other dialects like, postgres, MySql, etc
            dialect(libs.cashapp.sqldelight.dialect)

            generateAsync.set(true)

            schemaOutputDirectory = file("src/main/sqldelight/migrations")

            deriveSchemaFromMigrations = false
            treatNullAsUnknownForEquality = true
            generateAsync = true
        }
    }
}

buildConfig {
    buildConfigField("GITHUB_API_KEY")
    packageName = "okik.tech.jetcert"
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
        binaries.executable()
        useCommonJs()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
//        binaries.executable()
//        useCommonJs()
    }
    
    android {
       namespace = "okik.tech.jetcert.shared"
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
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android)
        }

        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.apollo.grapql.client)
            implementation("app.cash.sqldelight:runtime:2.3.2")
            implementation(libs.sqldelight.coroutines)
            implementation(libs.kotlinx.datetime)
            implementation(libs.apollo.datetime)

            implementation(project.dependencies.platform(libs.koin.bom))
//            implementation(libs.koin.core)
            implementation(libs.koin.cmp)
            implementation(libs.koin.cmp.viewmodel)
//            implementation("io.insert-koin:koin-core-viewmodel")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.jvm)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.wasm)
            implementation(libs.sqldelight.js)
            implementation(
                devNpm(
                    libs.plugins.webpack.get().pluginId,
                    libs.versions.webpack.get()
                )
            )
            implementation(
                npm(
                    libs.plugins.sqldelight.js.worker.get().pluginId,
                    libs.versions.js.worker.get()
                )
            )
            implementation(
                npm(
                    libs.plugins.sql.js.get().pluginId,
                    libs.versions.sql.js.get()
                )
            )
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.client.js)
            implementation(libs.sqldelight.js)
            // It provides a typesafe Domain-Specific Language (DSL) to build HTML and manipulate
            // the Document Object Model (DOM) directly in the browser using Kotlin
            implementation(libs.kotlinx.html)
            implementation(
                devNpm(
                    libs.plugins.webpack.get().pluginId,
                    libs.versions.webpack.get()
                )
            )
            implementation(
                npm(
                    libs.plugins.sqldelight.js.worker.get().pluginId,
                    libs.versions.js.worker.get()
                )
            )
            implementation(
                npm(
                    libs.plugins.sql.js.get().pluginId,
                    libs.versions.sql.js.get()
                )
            )
        }
    }
}

apollo {
    service("github") {
        // since apollo generates code we need to give the generated files a package name
        packageName.set("okik.tech.jetcert.apollo.generated")
        introspection {
            endpointUrl.set("https://api.github.com/graphql") // this is github's API
            // schema defines available  queries and mutations
            // then get github's schema, search for "github graphql schema' or just download it here "https://docs.github.com/public/fpt/schema.docs.graphql"
            // define where our schema files are locaed explictly,, rename the chema to match this fiel name, it is important to hage "graphqls" at the end
            schemaFile.set(File("src/main/graphql/schema.docs.graphqls"))
            // after adding schema rebuild the app(just run it and if you'd like install AS apollo plugin but you should), this will generate code that allows you to access the schema from the main common module
            // put the generated files(queries and mutations) in the same directory or in a subdirectory(you create them manually, you write them)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}