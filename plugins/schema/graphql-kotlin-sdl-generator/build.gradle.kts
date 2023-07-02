description = "GraphQL Kotlin SDL generator that can be used to generate GraphQL schema from source files."

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    implementation(projects.graphqlKotlinHooksProvider)
    implementation(projects.graphqlKotlinServer)
    implementation(projects.graphqlKotlinFederation)
    implementation(libs.classgraph)
    implementation(libs.slf4j)
    testImplementation(projects.graphqlKotlinSpringServer)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                implementation(project())
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
        executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))
    }
    jacocoTestCoverageVerification {
        dependsOn(testing.suites.named("integrationTest"))
        // we need to explicitly add integrationTest coverage info
        executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.85".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }
    check {
        dependsOn(testing.suites.named("integrationTest"))
    }
}
