import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Common code for running a GraphQL server in any HTTP server framework"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.benchmark)
}

dependencies {
    api(projects.graphqlKotlinSchemaGenerator)
    api(projects.graphqlKotlinDataloaderInstrumentation)
    api(projects.graphqlKotlinAutomaticPersistedQueries)
    api(libs.jackson)
    api(libs.fastjson2)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.logback)
}

// Benchmarks

// Create a separate source set for benchmarks.
sourceSets.create("benchmarks")

kotlin.sourceSets.getByName("benchmarks") {
    dependencies {
        implementation(libs.kotlinx.benchmark)
        implementation(sourceSets.main.get().output)
        implementation(sourceSets.main.get().runtimeClasspath)
    }
}

benchmark {
    configurations {
        register("graphQLRequest") {
            include("com.expediagroup.graphql.server.GraphQLServerRequest*")
        }
        register("graphQLResponse") {
            include("com.expediagroup.graphql.server.GraphQLServerResponse*")
        }
    }
    targets {
        register("benchmarks") {
            this as JvmBenchmarkTarget
        }
    }
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                excludes = listOf("com.expediagroup.graphql.server.testtypes.*")
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.84".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.73".toBigDecimal()
                }
            }
        }
    }
}
