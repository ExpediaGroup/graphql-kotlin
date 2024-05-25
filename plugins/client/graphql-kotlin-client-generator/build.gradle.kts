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
        exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
        exclude(group = "com.fasterxml.jackson.module", module = "jackson-module-kotlin")
    }
    implementation(libs.ktor.client.content)
    implementation(libs.slf4j)
    testImplementation(projects.graphqlKotlinClientJackson)
    testImplementation(projects.graphqlKotlinClientSerialization)
    testImplementation(libs.wiremock.lib)
    testImplementation(libs.compile.testing)
    testImplementation(libs.icu)
    testImplementation(libs.junit.params)
    // compile testing workaround -> explicit dependencies for compiler/annotation-processing
    testImplementation(libs.kotlin.annotation.processing)
    testImplementation(libs.kotlin.compiler)
    testImplementation(libs.kotlin.serialization)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.94".toBigDecimal()
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
