import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Common code for running a GraphQL server in any HTTP server framework"

val kotlinCoroutinesVersion: String by project
val kotlinxBenchmarkVersion: String by project
val jacksonVersion: String by project
val fastjson2Version: String by project

plugins {
    id("org.jetbrains.kotlinx.benchmark")
}

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api(project(path = ":graphql-kotlin-dataloader-instrumentation"))
    api(project(path = ":graphql-kotlin-automatic-persisted-queries"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.alibaba.fastjson2:fastjson2-kotlin:$fastjson2Version")
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
