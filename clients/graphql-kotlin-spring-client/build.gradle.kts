description = "A lightweight typesafe GraphQL HTTP Client based on Spring WebClient"

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api(project(path = ":graphql-kotlin-client-jackson"))
    api(libs.kotlinx.coroutines.reactor)
    api(libs.spring.webflux)
    api(libs.spring.boot.netty)
    testImplementation(project(path = ":graphql-kotlin-client-serialization"))
    testImplementation(libs.wiremock.jre8)
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
