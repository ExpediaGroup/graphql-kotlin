description = "An example spring service for federation that extends the basic types with new fields"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
}
