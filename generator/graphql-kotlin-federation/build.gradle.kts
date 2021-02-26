description = "Federated GraphQL schema generator"

val federationGraphQLVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api("com.apollographql.federation:federation-graphql-java-support:$federationGraphQLVersion")
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
