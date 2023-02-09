description = "An Example GraphQL service served by Ktor"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.examples.conventions")
    id("com.expediagroup.graphql")
    application
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-server")
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
    implementation(libs.kotlinx.coroutines.jdk8)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.ktor")
    }
}
