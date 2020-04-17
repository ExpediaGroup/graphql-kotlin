description = "GraphQL Kotlin Gradle plugin that can generate type-safe GraphQL Kotlin client"

plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

val kotlinCoroutinesVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":plugins:graphql-kotlin-plugin-core"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
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
            description = "Gradle Plugin that can generate type-safe GraphQL Kotlin client"
            tags = listOf("graphql", "kotlin", "graphql-client")
        }
    }
}
