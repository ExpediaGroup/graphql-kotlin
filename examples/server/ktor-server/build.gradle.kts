description = "An Example GraphQL service served by Ktor"

plugins {
    id("application")
    id("com.expediagroup.graphql")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-server")
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
    implementation(libs.kotlinx.coroutines.jdk8)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.ktor")
    }
}
