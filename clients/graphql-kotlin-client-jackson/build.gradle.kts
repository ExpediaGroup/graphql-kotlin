description = "GraphQL client serializer based on Jackson"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(libs.jackson)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.89".toBigDecimal()
                }
            }
        }
    }
}
