import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Common code for running a GraphQL server in any HTTP server framework"

val kotlinCoroutinesVersion: String by project
val kotlinxBenchmarkVersion: String by project

plugins {
    id("org.jetbrains.kotlinx.benchmark")
    kotlin("plugin.serialization")
}

val jacksonVersion: String by project
val kotlinxSerializationVersion: String by project
dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api(project(path = ":graphql-kotlin-dataloader-instrumentation"))
    api(project(path = ":graphql-kotlin-automatic-persisted-queries"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
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
                excludes = listOf("com.expediagroup.graphql.server.testtypes.*")
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.81".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.67".toBigDecimal()
                }
            }
        }
    }
}
