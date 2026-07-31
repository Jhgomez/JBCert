
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

    // The below setting is usually used to link outer libraries to the native(iOS) framework, in our case
    // the framework we are producing for our ios targets is static(isStatic = true) that changes how outher
    // dependencies are linked, but it is said that static framweorks are more performant, but if we make them
    // static and we don't link outer libraries, the system won't be able to execute our code, so there is different
    // solutions, one is to remove the "isStatic" flag in ios framwworks, which makes the dynamic by default
    // meaning they are not static by defualt, this solves linking errors, but we can do other things to keep
    // the framework static, all of them requireq to open the app in xcode, so first from xcode navigate
    // to the iosApp module and select the ".xcodeproj" file, when it is open you will see a gutter to the left
    // at the top of the gutter you will the name of the project in this case(if you didn't change it) "iosApp",
    // click it and now youll sse "PROJECT" and "TARGETS", the option I chooses is select the target "iosApp",
    // Build Phases > Link Binary With Libraries, click add and search "sqlite" you'll find two "libsqlite.tdb", choose
    // either or, I choosed "libsqlite3.tdb". According to other info you can do the same from the target options,
    // General > Frameworks, Libraries, and Embedded Content, click add and add the same, the other option, also
    // from target, Build Settings > Linking - General > Linking - General > Other LInker Flags and add the "-lsqlite3"
    // flag however it seems you need to choos the build type also, acording to info I found the modern approach is
    // thw two first approaches, After you do this, clear the build in xcode Product > Clean Build Folder and run the app
    // from xcode or AS, it should work now. If you wan to digg it yourself a litle you can search
    // thing like "how can i add sqlite3 c library in my swift app"
    //      linkSqlite = true
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
        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }

        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm {
        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
    
    js {
        browser()
        binaries.executable()
//        useCommonJs()

        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
//        useCommonJs()

        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
    
    android {
       namespace = "okik.tech.jetcert.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
           optIn.add("kotlin.time.ExperimentalTime")
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
            implementation(libs.apollo.adapters)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.sqldelight.primitives)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.cmp) // you can change this to koin-core
            implementation(libs.koin.cmp.viewmodel)

            implementation(libs.ksafe) // Note: kotlinx-serialization-json comes in transitively — don't add it yourself.
            implementation(libs.ksafe.compose)     // ← Compose state (optional)

            // commented out because they require to implement firebase and this project doesn't require creating a project in FIrebase
            // implementation(libs.kermit)
            // implementation(libs.kermit.crashalytics)
            // implementation(libs.kermit.crashalytics.ios)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.jvm)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        webMain.dependencies {
            implementation(libs.sqldelight.js)
            // it is important to add the webpack.config.d in all directories that are involved in the
            // web build process
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
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.wasm)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.client.js)
            // It provides a typesafe Domain-Specific Language (DSL) to build HTML and manipulate
            // the Document Object Model (DOM) directly in the browser using Kotlin
            implementation(libs.kotlinx.html)
        }
    }
}

apollo {
    service("github") {
        // since apollo generates code we need to give the generated files a package name
        packageName.set("okik.tech.jetcert.apollo")
        introspection {
            endpointUrl.set("https://api.github.com/graphql") // this is github's API
            // schema defines available  queries and mutations
            // then get github's schema, search for "github graphql schema' or just download it here "https://docs.github.com/public/fpt/schema.docs.graphql"
            // define where our schema files are locaed explictly,, rename the chema to match this fiel name, it is important to hage "graphqls" at the end
            schemaFile.set(File("src/main/graphql/schema.docs.graphqls"))
            // after adding schema rebuild the app(just run it and if you'd like install AS apollo plugin but you should), this will generate code that allows you to access the schema from the main common module
            // put the generated files(queries and mutations) in the same directory or in a subdirectory(you create them manually, you write them)
        }

        mapScalar("DateTime", "kotlin.time.Instant", "com.apollographql.adapter.core.KotlinInstantAdapter")
        mapScalar("URI", "kotlin.String")
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}