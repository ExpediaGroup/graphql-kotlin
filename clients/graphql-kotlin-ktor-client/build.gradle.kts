description = "A lightweight typesafe GraphQL HTTP Client based on Ktor HttpClient"

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api(project(path = ":graphql-kotlin-client-serialization"))
    api(libs.ktor.client.cio)
    api(libs.ktor.client.serialization)
    testImplementation(project(path = ":graphql-kotlin-client-jackson"))
    testImplementation(libs.ktor.client.logging)
    testImplementation(libs.ktor.client.okhttp)
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
