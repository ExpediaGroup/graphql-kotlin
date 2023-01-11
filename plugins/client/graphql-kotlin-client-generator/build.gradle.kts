description = "GraphQL Kotlin common utilities to generate a client."

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(libs.graphql.java) {
        exclude(group = "com.graphql-java", module = "java-dataloader")
    }
    api(libs.poet)
    api(libs.kotlinx.serialization.json)
    implementation(libs.jackson)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.serialization.jackson) {
        exclude("com.fasterxml.jackson.core", "jackson-databind")
        exclude("com.fasterxml.jackson.module", "jackson-module-kotlin")
    }
    implementation(libs.ktor.client.content)
    implementation(libs.slf4j)
    testImplementation(projects.graphqlKotlinClientJackson)
    testImplementation(projects.graphqlKotlinClientSerialization)
    testImplementation(libs.wiremock.jre8)
    testImplementation(libs.compile.testing)
    testImplementation(libs.icu)
    testImplementation(libs.junit.params)
    testImplementation(libs.kotlin.serialization)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.90".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
            }
        }
    }
}
