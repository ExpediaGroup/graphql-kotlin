description = "An example spring service for federation that extends the basic types with new fields"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    testImplementation(libs.spring.boot.test)

    graphqlSDL("com.expediagroup", "graphql-kotlin-federated-hooks-provider")
}

// config below is to simplify dockerfile
tasks.named("build") {
    dependsOn("bootJar")
    dependsOn("graphqlGenerateSDL")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("reviews-subgraph.jar")
}

graphql {
    schema {
        packages = listOf("com.expediagroup.graphql.examples.federation.reviews")
    }
}
