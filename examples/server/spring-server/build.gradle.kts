description = "An example GraphQL Spring server"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.examples.conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    implementation("com.expediagroup", "graphql-kotlin-hooks-provider")
    implementation(libs.spring.boot.validation)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.reactor.test)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.spring")
    }
}
