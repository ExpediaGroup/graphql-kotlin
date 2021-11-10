description = "Common code for running a GraphQL server in any HTTP server framework"

val kotlinCoroutinesVersion: String by project

plugins {
    id("org.jetbrains.kotlinx.benchmark") version "0.3.1"
}

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.3.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
}

benchmark {
    targets {
        register("test")
    }
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
