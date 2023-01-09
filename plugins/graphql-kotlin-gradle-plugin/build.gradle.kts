import java.time.LocalDate

description = "Gradle Kotlin Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    compileOnly(libs.android.plugin)

    compileOnly(project(":graphql-kotlin-client-generator"))
    compileOnly(project(":graphql-kotlin-sdl-generator"))

    testImplementation(libs.wiremock.jre8)
    testImplementation(libs.mustache)
    testImplementation(libs.junit.params)
}

java {
    withSourcesJar()
    if (rootProject.extra["isReleaseVersion"] as Boolean) {
        withJavadocJar()
    }
}

gradlePlugin {
    plugins {
        register("graphQLPlugin") {
            id = "com.expediagroup.graphql"
            displayName = "GraphQL Kotlin Gradle Plugin"
            description = "Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"
            implementationClass = "com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://expediagroup.github.io/graphql-kotlin"
    vcsUrl = "https://github.com/ExpediaGroup/graphql-kotlin"
    tags = listOf("graphql", "kotlin", "graphql-client", "schema-generator", "sdl")
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
    test {
        // ensure we always run tests by setting new inputs
        //
        // tests are parameterized and run IT based on projects under src/integration directories
        // Gradle is unaware of this and does not run tests if no sources/inputs changed
        inputs.property("integration.date", LocalDate.now())

        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
        dependsOn(":resolveIntegrationTestDependencies")

        systemProperty("androidPluginVersion", libs.versions.android.plugin.get())
        systemProperty("kotlinVersion", libs.versions.kotlin.get())
        systemProperty("springBootVersion", libs.versions.spring.boot.get())
        systemProperty("junitVersion", libs.versions.junit.get())
    }

    publishing {
        afterEvaluate {
            publications {
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
}
