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
        kotlin("plugin.serialization") version kotlinVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.springframework.boot") version springBootVersion
    }
}

rootProject.name = "graphql-kotlin-examples"

// composite builds
includeBuild("..")

// client examples
include(":client-examples")
include(":client-examples:gradle-client-example")
include(":client-examples:maven-client-example")
include(":client-examples:server-client-example")

// federation examples
include(":base-app")
include(":extend-app")

// server examples
include(":ktor-server")
include(":spring-server")

// project mappings
project(":client-examples").projectDir = file("client")
project(":client-examples:gradle-client-example").projectDir = file("client/gradle-client")
project(":client-examples:maven-client-example").projectDir = file("client/maven-client")
project(":client-examples:server-client-example").projectDir = file("client/server")

project(":base-app").projectDir = file("federation/base-app")
project(":extend-app").projectDir = file("federation/extend-app")

project(":spring-server").projectDir = file("server/spring-server")
project(":ktor-server").projectDir = file("server/ktor-server")
