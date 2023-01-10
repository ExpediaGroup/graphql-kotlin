description = "Federated GraphQL schema generator"

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api(libs.federation)
    testImplementation(libs.reactor.core)
    testImplementation(libs.reactor.extensions)
    testImplementation(libs.junit.params)
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
                    minimum = "0.90".toBigDecimal()
                }
            }
        }
    }
}
