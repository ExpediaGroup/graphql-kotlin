import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.graphql

description = "Example usage of Gradle plugin to generate GraphQL Kotlin Client"

plugins {
    application
    kotlin("plugin.serialization")
    id("com.expediagroup.graphql")
}

val ktorVersion: String by project
dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-client")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
}

application {
    mainClass.set("com.expediagroup.graphql.examples.client.gradle.ApplicationKt")
}

val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
graphql {
    client {
        packageName = "com.expediagroup.graphql.generated"
        // you can also use direct sdlEndpoint instead
        sdlEndpoint = "http://localhost:$wireMockServerPort/sdl"

        // optional
        allowDeprecatedFields = true
        headers = mapOf("X-Custom-Header" to "My-Custom-Header")
        customScalars = listOf(GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.examples.client.gradle.UUIDScalarConverter"))
    }
}
ktlint {
    filter {
        exclude("**/generated/**")
    }
}
