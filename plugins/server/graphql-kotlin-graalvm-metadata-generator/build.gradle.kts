description = "GraalVM metadata generator for GraphQL Kotlin servers"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    implementation(projects.graphqlKotlinHooksProvider)
    implementation(projects.graphqlKotlinServer)
    implementation(projects.graphqlKotlinFederation)
    implementation(libs.classgraph)
    implementation(libs.slf4j)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
                implementation(libs.junit.platform.launcher)
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }

            sources {
                java {
                    setSrcDirs(listOf("src/integrationTest/kotlin"))
                }
                resources {
                    setSrcDirs(listOf("src/integrationTest/resources"))
                }
                compileClasspath += sourceSets["test"].compileClasspath
                runtimeClasspath += compileClasspath + sourceSets["test"].runtimeClasspath
            }
        }
    }
}

tasks {
    jacocoTestReport {
        dependsOn(testing.suites.named("integrationTest"))
        // we need to explicitly add integrationTest coverage info
        executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
    }
    jacocoTestCoverageVerification {
        dependsOn(testing.suites.named("integrationTest"))
        // we need to explicitly add integrationTest coverage info
        executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.89".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.66".toBigDecimal()
                }
            }
        }
    }
    check {
        dependsOn(testing.suites.named("integrationTest"))
    }
}
