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
