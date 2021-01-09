description = "Gradle Kotlin Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

val kotlinCoroutinesVersion: String by project
val wireMockVersion: String by project
val mustacheVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-plugin-core"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
    testImplementation("com.github.spullara.mustache.java:compiler:$mustacheVersion")
}

gradlePlugin {
    plugins {
        register("graphQLPlugin") {
            id = "com.expediagroup.graphql"
            implementationClass = "com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://expediagroup.github.io/graphql-kotlin"
    vcsUrl = "https://github.com/ExpediaGroup/graphql-kotlin"

    (plugins) {
        "graphQLPlugin" {
            displayName = "GraphQL Kotlin Gradle Plugin"
            description = "Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"
            tags = listOf("graphql", "kotlin", "graphql-client", "schema-generator", "sdl")
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src")
        }
    }
}

tasks {
    val generateDefaultVersion by registering {
        val fileName = "PluginVersion.kt"
        val defaultVersionFile = File("$buildDir/generated/src/com/expediagroup/graphql/plugin/gradle", fileName)

        inputs.property(fileName, project.version)
        outputs.file(defaultVersionFile)

        doFirst {
            defaultVersionFile.parentFile.mkdirs()
            defaultVersionFile.writeText(
                """
                package com.expediagroup.graphql.plugin.gradle
                internal const val DEFAULT_PLUGIN_VERSION = "${project.version}"

                """.trimIndent()
            )
        }
    }

    compileKotlin {
        dependsOn(generateDefaultVersion)
    }
    publishPlugins {
        doFirst {
            System.setProperty("gradle.publish.key", System.getenv("PLUGIN_PORTAL_KEY"))
            System.setProperty("gradle.publish.secret", System.getenv("PLUGIN_PORTAL_SECRET"))
        }
    }
    publishing {
        publications {
            afterEvaluate {
                named<MavenPublication>("graphQLPluginPluginMarkerMaven") {
                    // update auto-generated pom.xml for plugin marker with required information
                    pom {
                        name.set(artifactId)
                        description.set("Plugin descriptor for GraphQL Kotlin Gradle plugin")
                    }
                }
            }
        }
    }
    test {
        dependsOn(":resolveIntegrationTestDependencies")

        val kotlinVersion: String by project
        val junitVersion: String by project
        val springBootVersion: String by project
        systemProperty("kotlinVersion", kotlinVersion)
        systemProperty("springBootVersion", springBootVersion)
        systemProperty("junitVersion", junitVersion)
    }
}
