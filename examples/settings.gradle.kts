pluginManagement {
    val properties = java.util.Properties()
    properties.load(File(rootDir.parent, "gradle.properties").inputStream())

    val detektVersion: String by properties
    val kotlinVersion: String by properties
    val ktlintPluginVersion: String by properties
    val springBootVersion: String by properties

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.springframework.boot") version springBootVersion
    }
}

rootProject.name = "graphql-kotlin-examples"

includeBuild("..")
include(":federation:base-app")
include(":federation:extend-app")
include(":server:ktor-server")
include(":server:spring-server")

project(":federation:base-app").projectDir = file("federation/base-app")
project(":federation:extend-app").projectDir = file("federation/extend-app")
project(":server:spring-server").projectDir = file("server/spring-server")
project(":server:ktor-server").projectDir = file("server/ktor-server")
