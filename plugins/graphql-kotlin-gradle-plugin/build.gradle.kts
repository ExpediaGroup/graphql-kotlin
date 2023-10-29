description = "Gradle Kotlin Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

plugins {
    id("com.expediagroup.graphql.conventions")
    `java-gradle-plugin`
    alias(libs.plugins.plugin.publish)
}

dependencies {
    implementation(libs.kotlin.gradle.api)

    compileOnly(libs.android.plugin)
    compileOnly(libs.graalvm.plugin)
    compileOnly(projects.graphqlKotlinClientGenerator)
    compileOnly(projects.graphqlKotlinSdlGenerator)
    compileOnly(projects.graphqlKotlinGraalvmMetadataGenerator)

    testImplementation(libs.wiremock.jre8)
    testImplementation(libs.junit.params)
}

java {
    withSourcesJar()
    if (rootProject.extra["isReleaseVersion"] as Boolean) {
        withJavadocJar()
    }
}

gradlePlugin {
    website.set("https://opensource.expediagroup.com/graphql-kotlin/docs/")
    vcsUrl.set("https://github.com/ExpediaGroup/graphql-kotlin")
    plugins {
        register("graphQLPlugin") {
            id = "com.expediagroup.graphql"
            displayName = "GraphQL Kotlin Gradle Plugin"
            description = "Gradle Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"
            implementationClass = "com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin"
            tags.set(listOf("graphql", "kotlin", "graphql-client", "schema-generator", "sdl"))
        }
    }
}

val generateDefaultVersion by tasks.registering {
    val fileName = "PluginVersion.kt"
    val defaultVersionFile = File("$buildDir/generated/src/com/expediagroup/graphql/plugin/gradle", fileName)

    inputs.property(fileName, project.version)
    outputs.dir(defaultVersionFile.parent)

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

sourceSets {
    main {
        java {
            srcDir(generateDefaultVersion)
        }
    }
}

tasks {
    publishPlugins {
        doFirst {
            System.setProperty("gradle.publish.key", System.getenv("PLUGIN_PORTAL_KEY"))
            System.setProperty("gradle.publish.secret", System.getenv("PLUGIN_PORTAL_SECRET"))
        }
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
