import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Common code for running a GraphQL server in any HTTP server framework"

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.benchmark)
}

dependencies {
    api(projects.graphqlKotlinSchemaGenerator)
    api(projects.graphqlKotlinDataloaderInstrumentation)
    api(projects.graphqlKotlinAutomaticPersistedQueries)
    api(libs.jackson)
    testImplementation(libs.kotlinx.coroutines.test)
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
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.95".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.84".toBigDecimal()
                }
            }
        }
    }
}
