description = "A lightweight typesafe GraphQL HTTP Client based on Ktor HttpClient"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
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
