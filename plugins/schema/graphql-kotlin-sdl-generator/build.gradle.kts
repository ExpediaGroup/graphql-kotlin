description = "GraphQL Kotlin SDL generator that can be used to generate GraphQL schema from source files."

val classGraphVersion: String by project
val slf4jVersion: String by project

dependencies {
    implementation(project(path = ":graphql-kotlin-hooks-provider"))
    implementation(project(path = ":graphql-kotlin-server"))
    implementation(project(path = ":graphql-kotlin-federation"))
    implementation("io.github.classgraph:classgraph:$classGraphVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation(project(path = ":graphql-kotlin-spring-server"))
}

sourceSets {
    create("integrationTest") {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("src/integrationTest/kotlin")
            resources.srcDir("src/integrationTest/resources")
            compileClasspath += sourceSets["main"].output + sourceSets["test"].compileClasspath
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}

tasks {
    val integrationTest by registering(Test::class) {
        description = "Runs the integration tests"
        group = "verification"

        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        mustRunAfter("test")
        finalizedBy("jacocoTestReport")
        useJUnitPlatform()
    }

    jacocoTestReport {
        // we need to explicitly add integrationTest coverage info
        executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))
    }
    jacocoTestCoverageVerification {
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
        dependsOn("integrationTest")
    }
}
