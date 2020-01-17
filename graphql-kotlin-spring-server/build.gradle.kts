description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.jetbrains.kotlin.kapt")
}

val kotlinCoroutinesVersion: String by project
val springBootVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-federation"))
    api("org.springframework.boot:spring-boot-starter-webflux:$springBootVersion")
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")
    api("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
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
                    minimum = "0.96".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.91".toBigDecimal()
                }
            }
        }
    }
}
