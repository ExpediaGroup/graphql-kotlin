description = "GraphQL plugin for Ktor servers"

//@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinServer)
    api(projects.graphqlKotlinFederation)
    api(libs.ktor.serialization.jackson)
    api(libs.ktor.server.core)
    api(libs.ktor.server.content)
}
