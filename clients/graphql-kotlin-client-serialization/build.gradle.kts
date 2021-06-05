description = "GraphQL client serializer based on kotlinx.serialization"

plugins {
    kotlin("plugin.serialization")
}

val kotlinCoroutinesVersion: String by project
val kotlinxSerializationVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.74".toBigDecimal()
                }
            }
        }
    }
}
