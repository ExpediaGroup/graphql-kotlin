description = "GraphQL Kotlin gradle plugin"

plugins {
    `java-gradle-plugin`
}

val ktorVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
}

gradlePlugin {
    plugins {
        register("graphQLPlugin") {
            id = "com.expediagroup.graphql"
            implementationClass = "com.expediagroup.graphql.plugin.gradle.GraphQLGradlePlugin"
        }
    }
}
