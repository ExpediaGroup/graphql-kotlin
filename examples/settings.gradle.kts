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
include(":spring-example")
include(":federation-example")
include(":federation-example:base-app")
include(":federation-example:extend-app")
include(":spark-example")
include(":ktor-server")

project(":spring-example").projectDir = file("spring")
project(":federation-example").projectDir = file("federation")
project(":federation-example:base-app").projectDir = file("federation/base-app")
project(":federation-example:extend-app").projectDir = file("federation/extend-app")
project(":spark-example").projectDir = file("spark")
project(":ktor-server").projectDir = file("ktor-server")
