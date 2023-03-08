@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    application
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-server")
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
}
