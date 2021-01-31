pluginManagement {
    val detektVersion: String by settings
    val dokkaVersion: String by settings
    val kotlinVersion: String by settings
    val ktlintPluginVersion: String by settings
    val mavenPluginDevelopmentVersion: String by settings
    val nexusPublishPluginVersion: String by settings
    val pluginPublishPluginVersion: String by settings
    val springBootVersion: String by settings
    val stagingPluginVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("com.gradle.plugin-publish") version pluginPublishPluginVersion
        id("de.benediktritter.maven-plugin-development") version mavenPluginDevelopmentVersion
        id("de.marcphilipp.nexus-publish") version nexusPublishPluginVersion
        id("io.codearte.nexus-staging") version stagingPluginVersion
        id("io.gitlab.arturbosch.detekt") version detektVersion
        id("org.jetbrains.dokka") version dokkaVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintPluginVersion
        id("org.springframework.boot") version springBootVersion
    }
}

rootProject.name = "graphql-kotlin"

// Clients
include(":graphql-kotlin-client")
include(":graphql-kotlin-client-multiplatform")
include(":graphql-kotlin-ktor-client")
include(":graphql-kotlin-ktor-client-multiplatform")
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

// Types
include(":graphql-kotlin-types")
include(":graphql-kotlin-types-multiplatform")

//
// Project mappings so we don't need to create projects that group subprojects
//

// Clients
project(":graphql-kotlin-client").projectDir = file("clients/graphql-kotlin-client")
project(":graphql-kotlin-client-multiplatform").projectDir = file("clients/graphql-kotlin-client-multiplatform")
project(":graphql-kotlin-ktor-client").projectDir = file("clients/graphql-kotlin-ktor-client")
project(":graphql-kotlin-ktor-client-multiplatform").projectDir = file("clients/graphql-kotlin-ktor-client-multiplatform")
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

// Types
project(":graphql-kotlin-types").projectDir = file("types/graphql-kotlin-types")
project(":graphql-kotlin-types-multiplatform").projectDir = file("types/graphql-kotlin-types-multiplatform")

// Servers
project(":graphql-kotlin-server").projectDir = file("servers/graphql-kotlin-server")
project(":graphql-kotlin-spring-server").projectDir = file("servers/graphql-kotlin-spring-server")
