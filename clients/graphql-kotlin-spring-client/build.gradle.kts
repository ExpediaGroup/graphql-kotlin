description = "A lightweight typesafe GraphQL HTTP Client based on Spring WebClient"

plugins {
    kotlin("plugin.serialization")
}

val kotlinxCoroutinesVersion: String by project
val springVersion: String by project
val springBootVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api(project(path = ":graphql-kotlin-client-jackson"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinxCoroutinesVersion")
    api("org.springframework:spring-webflux:$springVersion")
    api("org.springframework.boot:spring-boot-starter-reactor-netty:$springBootVersion")
    testImplementation(project(path = ":graphql-kotlin-client-serialization"))
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.95".toBigDecimal()
                }
            }
        }
    }
}
