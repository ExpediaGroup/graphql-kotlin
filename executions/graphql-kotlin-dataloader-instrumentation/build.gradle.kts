description = "Data Loader Instrumentations"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinDataloader)
    api(libs.graphql.java) {
        exclude(group = "com.graphql-java", module = "java-dataloader")
    }
    testImplementation(libs.reactor.core)
    testImplementation(libs.reactor.extensions)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.93".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.70".toBigDecimal()
                }
            }
        }
    }
}
