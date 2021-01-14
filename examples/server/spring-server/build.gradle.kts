description = "An example GraphQL Spring server"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.expediagroup.graphql")
}

val springBootVersion: String by project
val reactorVersion: String by project

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    implementation("com.expediagroup", "graphql-kotlin-hooks-provider")
    implementation("org.springframework.boot", "spring-boot-starter-validation", springBootVersion)
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.server.spring")
    }
}
