plugins {
    id("com.expediagroup.it-conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
}

tasks.register("run") {
    dependsOn("bootRun")
}
