import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Common code for running a GraphQL server in any HTTP server framework"

val kotlinCoroutinesVersion: String by project
val kotlinxBenchmarkVersion: String by project

plugins {
    id("org.jetbrains.kotlinx.benchmark") version "0.4.0"
}

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
}

// Benchmarks

// Create a separate source set for benchmarks.
sourceSets.create("benchmarks")

kotlin.sourceSets.getByName("benchmarks") {
    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:$kotlinxBenchmarkVersion")
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
                    minimum = "0.85".toBigDecimal()
                }
            }
        }
    }
}
