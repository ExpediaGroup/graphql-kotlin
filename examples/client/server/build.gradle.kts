plugins {
    id("com.expediagroup.graphql.examples.conventions")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
}
