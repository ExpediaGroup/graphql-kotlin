description = "An Example GraphQL service served by Ktor"

plugins {
    id("application")
    id("com.expediagroup.graphql")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

val kotlinxCoroutinesVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-server")
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinxCoroutinesVersion)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.ktor")
    }
}
