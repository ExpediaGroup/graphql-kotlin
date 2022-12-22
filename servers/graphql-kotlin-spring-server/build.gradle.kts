description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
}

val kotlinxCoroutinesVersion: String by project
val springBootVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-server"))
    api(project(path = ":graphql-kotlin-federation"))
    api("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutinesVersion")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinxCoroutinesVersion")
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.86".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.66".toBigDecimal()
                }
            }
        }
    }
}
