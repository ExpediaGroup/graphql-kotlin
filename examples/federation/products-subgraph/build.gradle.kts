description = "An example spring service for federation that implements product type"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.expediagroup.graphql")
}

val springBootVersion: String by project
dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    testImplementation("org.springframework.boot", "spring-boot-starter-test", springBootVersion)

    graphqlSDL("com.expediagroup", "graphql-kotlin-federated-hooks-provider")
}

// config below is to simplify dockerfile
tasks.named("build") {
    dependsOn("bootJar")
    dependsOn("graphqlGenerateSDL")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("products-subgraph.jar")
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.federation.products")
    }
}
