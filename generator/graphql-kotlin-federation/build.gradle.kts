description = "Federated GraphQL schema generator"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinSchemaGenerator)
    api(libs.federation) {
        exclude(group = "com.graphql-java", module = "graphql-java")
    }
    api(libs.graphql.java)
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
                    minimum = "0.95".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.83".toBigDecimal()
                }
            }
        }
    }
}
