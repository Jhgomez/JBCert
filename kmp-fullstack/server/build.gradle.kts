plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
}

group = "okik.tech.fullstack"
version = "1.0.0"
application {
    mainClass = "okik.tech.fullstack.ApplicationKt"
}

dependencies {
    api(project(":core"))
    implementation(libs.logback)
    implementation(libs.kotlinx.serialization)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.json)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.status.pages)
    implementation(libs.ktor.koin)
    implementation(libs.ktor.koin.slf4j)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio.engine)
    implementation(libs.ktor.negotiation.client)
    implementation(libs.ktor.logging.client)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    implementation(libs.sqlite.and.jdbc.driver)
    implementation(libs.hikari.connection.pool)
    
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}