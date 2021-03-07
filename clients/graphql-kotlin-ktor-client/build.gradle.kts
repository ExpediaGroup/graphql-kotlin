description = "A lightweight typesafe GraphQL HTTP Client based on Ktor HttpClient"

plugins {
    kotlin("plugin.serialization")
}

val ktorVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api(project(path = ":graphql-kotlin-client-jackson"))
    api("io.ktor:ktor-client-cio:$ktorVersion")
    api("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation(project(path = ":graphql-kotlin-client-serialization"))
    testImplementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    testImplementation("io.ktor:ktor-client-logging:$ktorVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
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
