description = "A lightweight typesafe GraphQL HTTP Client based on Spring WebClient"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(projects.graphqlKotlinClientJackson)
    api(libs.kotlinx.coroutines.reactor)
    api(libs.spring.webflux)
    api(libs.spring.boot.netty)
    testImplementation(projects.graphqlKotlinClientSerialization)
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
