description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    id("org.jetbrains.kotlin.plugin.spring")
}

val kotlinCoroutinesVersion: String by project
val springBootVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-federation"))
    api("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion")
    // TODO change below from api to implementation?
    api("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}
