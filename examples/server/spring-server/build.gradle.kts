description = "An example GraphQL Spring server"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    implementation("com.expediagroup", "graphql-kotlin-hooks-provider")
    implementation(libs.spring.boot.validation)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.reactor.test)
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.spring")
    }
}
