description = "GraphQL Kotlin Gradle plugin"

plugins {
    `java-gradle-plugin`
}

dependencies {
    api(project(path = ":plugins:graphql-kotlin-plugin-core"))
}

gradlePlugin {
    plugins {
        register("graphQLPlugin") {
            id = "com.expediagroup.graphql"
            implementationClass = "com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin"
        }
    }
}
