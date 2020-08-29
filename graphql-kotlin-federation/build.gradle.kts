description = "Federated GraphQL schema generator"

val kotlinVersion: String by project
val junitVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    implementation(kotlin("reflect", kotlinVersion))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
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
                    minimum = "0.94".toBigDecimal()
                }
            }
        }
    }
}
