description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
}

dependencies {
    api(project(path = ":graphql-kotlin-server"))
    api(project(path = ":graphql-kotlin-federation"))
    api(libs.spring.boot.webflux)
    api(libs.kotlinx.coroutines.jdk8)
    api(libs.kotlinx.coroutines.reactor)
    api(libs.reactor.extensions)
    kapt(libs.spring.boot.config)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.reactor.test)
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
