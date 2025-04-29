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
                    minimum = "0.94".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
}
