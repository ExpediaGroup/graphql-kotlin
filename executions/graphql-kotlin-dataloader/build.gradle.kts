description = "Graphql Kotlin Data Loader"

dependencies {
    api(libs.dataloader)
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
                    minimum = "0.52".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.50".toBigDecimal()
                }
            }
        }
    }
}
