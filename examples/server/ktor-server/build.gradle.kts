description = "An Example GraphQL service served by Ktor"

plugins {
    id("application")
    id("com.expediagroup.graphql")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

val kotlinCoroutinesVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-server")
    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinCoroutinesVersion)
    // https://ktor.io/docs/testing.html#add-dependencies
    testImplementation("io.ktor", "ktor-server-test-host", ktorVersion)
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.ktor")
    }
}
