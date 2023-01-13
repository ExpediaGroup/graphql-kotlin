description = "Code-only GraphQL schema generation for Kotlin"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(libs.graphql.java)
    api(libs.kotlinx.coroutines.reactive)
    implementation(libs.classgraph)
    implementation(libs.slf4j)
    testImplementation(libs.rxjava)
    testImplementation(libs.junit.params)
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
                    minimum = "0.93".toBigDecimal()
                }
            }
        }
    }
}
