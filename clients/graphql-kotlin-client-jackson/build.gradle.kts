description = "GraphQL client serializer based on Jackson"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(project(path = ":graphql-kotlin-client"))
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
