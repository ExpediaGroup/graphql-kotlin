description = "GraphQL client serializer based on kotlinx.serialization"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    // increase it when https://github.com/Kotlin/kotlinx.serialization/issues/961 is resolved
                    minimum = "0.70".toBigDecimal()
                }
            }
        }
    }
}
