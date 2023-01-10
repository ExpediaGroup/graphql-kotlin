import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.graphql

description = "Example usage of Gradle plugin to generate GraphQL Kotlin Client"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.examples.conventions")
    id("com.expediagroup.graphql")
    application
    alias(libs.plugins.kotlin.serialization)
}

val ktorVersion: String by project
dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-client")
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.jvm.logging)
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
        customScalars = listOf(
            GraphQLScalar("_Any", "kotlinx.serialization.json.JsonObject", "com.expediagroup.graphql.examples.client.gradle.AnyScalarConverter"),
            GraphQLScalar("Locale", "com.ibm.icu.util.ULocale", "com.expediagroup.graphql.examples.client.gradle.ULocaleScalarConverter"),
            GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.examples.client.gradle.UUIDScalarConverter"),
        )
        serializer = GraphQLSerializer.KOTLINX
    }
}
ktlint {
    filter {
        exclude("**/generated/**")
    }
}
