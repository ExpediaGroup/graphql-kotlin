pluginManagement {
    val detektVersion: String by settings
    val dokkaVersion: String by settings
    val kotlinVersion: String by settings
    val ktlintPluginVersion: String by settings
    val nexusPublishPluginVersion: String by settings
    val springBootVersion: String by settings
    val stagingPluginVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.springframework.boot") version springBootVersion
        id("io.codearte.nexus-staging") version stagingPluginVersion
        id("de.marcphilipp.nexus-publish") version nexusPublishPluginVersion
    }
}

rootProject.name = "graphql-kotlin"

include(":graphql-kotlin-schema-generator")
include(":graphql-kotlin-federation")
include(":graphql-kotlin-spring-server")
