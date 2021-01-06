description = "Common code for running a GraphQL server in any HTTP server framework"

val kotlinCoroutinesVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-types"))
    api(project(path = ":graphql-kotlin-schema-generator"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
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
