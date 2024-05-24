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
    api(libs.ktor.server.websockets)
    api(libs.ktor.server.statuspages)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.content)
    testImplementation(libs.ktor.client.websockets)
    testImplementation(libs.ktor.server.cio)
    testImplementation(libs.ktor.server.test.host)
    constraints {
        implementation(libs.commons.codec) {
            because("Cxeb68d52e-5509: Apache commons-codec before 1.13 is vulnerable to information exposure. https://devhub.checkmarx.com/cve-details/Cxeb68d52e-5509/")
        }
    }
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.48".toBigDecimal()
                }
            }
        }
    }
}
