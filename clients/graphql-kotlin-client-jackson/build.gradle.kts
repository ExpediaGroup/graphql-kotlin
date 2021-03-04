val jacksonVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.85".toBigDecimal()
                }
            }
        }
    }
}
