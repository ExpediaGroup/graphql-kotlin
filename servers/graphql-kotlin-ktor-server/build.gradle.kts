description = "GraphQL plugin for Ktor servers"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinServer)
    api(projects.graphqlKotlinFederation)
    api(libs.ktor.serialization.jackson)
    api(libs.ktor.server.core)
    api(libs.ktor.server.content)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.content)
    testImplementation(libs.ktor.server.cio)
    testImplementation(libs.ktor.server.test.host)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.60".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.35".toBigDecimal()
                }
            }
        }
    }
}
