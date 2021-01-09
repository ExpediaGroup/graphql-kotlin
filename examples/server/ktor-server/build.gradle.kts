val ktor_version: String by project
val logback_version: String by project
val slf4j_version: String by project

description = "An Example GraphQL service served by Ktor"

plugins {
    id("application")
    id("com.expediagroup.graphql")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

val kotlinCoroutinesVersion: String by project
dependencies {
    implementation("com.expediagroup", "graphql-kotlin-schema-generator")
    implementation("com.expediagroup", "graphql-kotlin-types")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinCoroutinesVersion)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.ktor")
    }
}
