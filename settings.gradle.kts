pluginManagement {
    val detektVersion: String by settings
    val dokkaVersion: String by settings
    val kotlinVersion: String by settings
    val ktlintPluginVersion: String by settings
    val mavenPluginDevelopmentVersion: String by settings
    val nexusPublishPluginVersion: String by settings
    val pluginPublishPluginVersion: String by settings
    val springBootVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("com.gradle.plugin-publish") version pluginPublishPluginVersion
        id("de.benediktritter.maven-plugin-development") version mavenPluginDevelopmentVersion
        id("io.github.gradle-nexus.publish-plugin") version nexusPublishPluginVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.springframework.boot") version springBootVersion
    }
}

rootProject.name = "graphql-kotlin"

// Clients
include(":graphql-kotlin-client")
include(":graphql-kotlin-client-serialization")
include(":graphql-kotlin-client-jackson")
include(":graphql-kotlin-ktor-client")
include(":graphql-kotlin-spring-client")

// Generator
include(":graphql-kotlin-schema-generator")
include(":graphql-kotlin-federation")

// Plugins
include(":graphql-kotlin-gradle-plugin")
include(":graphql-kotlin-maven-plugin")
include(":graphql-kotlin-client-generator")
include(":graphql-kotlin-sdl-generator")
include(":graphql-kotlin-hooks-provider")
include(":graphql-kotlin-federated-hooks-provider")

// Servers
include(":graphql-kotlin-server")
include(":graphql-kotlin-spring-server")

// Executions
include(":transaction-batcher")

//
// Project mappings so we don't need to create projects that group subprojects
//

// Clients
project(":graphql-kotlin-client").projectDir = file("clients/graphql-kotlin-client")
project(":graphql-kotlin-client-serialization").projectDir = file("clients/graphql-kotlin-client-serialization")
project(":graphql-kotlin-client-jackson").projectDir = file("clients/graphql-kotlin-client-jackson")
project(":graphql-kotlin-ktor-client").projectDir = file("clients/graphql-kotlin-ktor-client")
project(":graphql-kotlin-spring-client").projectDir = file("clients/graphql-kotlin-spring-client")

// Generator
project(":graphql-kotlin-schema-generator").projectDir = file("generator/graphql-kotlin-schema-generator")
project(":graphql-kotlin-federation").projectDir = file("generator/graphql-kotlin-federation")

// Plugins
project(":graphql-kotlin-gradle-plugin").projectDir = file("plugins/graphql-kotlin-gradle-plugin")
project(":graphql-kotlin-maven-plugin").projectDir = file("plugins/graphql-kotlin-maven-plugin")
project(":graphql-kotlin-client-generator").projectDir = file("plugins/client/graphql-kotlin-client-generator")
project(":graphql-kotlin-sdl-generator").projectDir = file("plugins/schema/graphql-kotlin-sdl-generator")
project(":graphql-kotlin-hooks-provider").projectDir = file("plugins/schema/graphql-kotlin-hooks-provider")
project(":graphql-kotlin-federated-hooks-provider").projectDir = file("plugins/schema/graphql-kotlin-federated-hooks-provider")

// Servers
project(":graphql-kotlin-server").projectDir = file("servers/graphql-kotlin-server")
project(":graphql-kotlin-spring-server").projectDir = file("servers/graphql-kotlin-spring-server")

// Executions
project(":transaction-batcher").projectDir = file("executions/transaction-batcher")
