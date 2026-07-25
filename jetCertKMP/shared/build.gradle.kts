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
}

//sqldelight {
//    databases {
//        register("jetcertDB") {
//            packageName.set("okik.tech.jetcert.db")
//            dialect("{{ dialect }}:{{ versions.sqldelight }}")
//            generateAsync.set(true){% endif %}
//        }
//    }
//}

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
//        =================
        binaries.executable()
        useCommonJs()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
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
            implementation(libs.sqldelight)
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
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.sqldelight.js)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.wasm)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.ktor.client.js)
//            ============================================
            implementation("app.cash.sqldelight:web-worker-driver")
            implementation("app.cash.sqldelight:primitive-adapters")
            implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.12.0")
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.2.1"))
            implementation(npm("sql.js", "1.8.0"))

//            val sqljsWorker = file("${gradle.includedBuild("sqldelight").projectDir}/drivers/web-worker-driver/sqljs")
//            implementation(npm("@cashapp/sqldelight-sqljs-worker", sqljsWorker))
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