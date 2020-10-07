description = "An example spring service for federation that implements some basic types"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
}
