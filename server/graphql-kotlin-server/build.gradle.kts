description = "Common code for running a GraphQL server in any HTTP server framework"

dependencies {
    api(project(path = ":graphql-kotlin-types"))
    api(project(path = ":graphql-kotlin-schema-generator"))
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
                    minimum = "0.85".toBigDecimal()
                }
            }
        }
    }
}
