description = "A lightweight typesafe GraphQL HTTP Client based on Ktor HttpClient"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(projects.graphqlKotlinClientSerialization)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.serialization)
    testImplementation(projects.graphqlKotlinClientJackson)
    testImplementation(libs.ktor.client.logging)
    testImplementation(libs.ktor.client.okhttp)
    testImplementation(libs.wiremock.lib)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.97".toBigDecimal()
                }
            }
        }
    }
}
