import kotlinx.benchmark.gradle.JvmBenchmarkTarget

description = "Code-only GraphQL schema generation for Kotlin"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.benchmark)
}

dependencies {
    api(libs.graphql.java)
    api(libs.kotlinx.coroutines.reactive)
    implementation(libs.classgraph)
    implementation(libs.slf4j)
    testImplementation(libs.rxjava)
}

// Benchmarks

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
        register("schemaBuilder") {
            include("com.expediagroup.graphql.generator.GraphQLSchemaBuilderBenchmark")
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
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.92".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.90".toBigDecimal()
                }
            }
        }
    }
}
