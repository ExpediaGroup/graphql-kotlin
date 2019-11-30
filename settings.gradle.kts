pluginManagement {
    val detektVersion: String by settings
    val dokkaVersion: String by settings
    val kotlinVersion: String by settings
    val ktlintPluginVersion: String by settings
    val springBootVersion: String by settings
    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.springframework.boot") version springBootVersion
    }
}

rootProject.name = "graphql-kotlin"

include(":graphql-kotlin-schema-generator")
include(":graphql-kotlin-federation")
include(":graphql-kotlin-spring-server")
include(":examples")
include(":examples:spring-example")
include(":examples:federation")
include(":examples:federation:base-app")
include(":examples:federation:extend-app")

project(":examples:spring-example").projectDir = file("examples/spring")
project(":examples:federation").projectDir = file("examples/federation")
project(":examples:federation:base-app").projectDir = file("examples/federation/base-app")
project(":examples:federation:extend-app").projectDir = file("examples/federation/extend-app")
