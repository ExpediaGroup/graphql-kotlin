description = "Code-only GraphQL schema generation for Kotlin"

val classGraphVersion: String by project
val graphQLJavaVersion: String by project
val jacksonVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val rxjavaVersion: String by project

dependencies {
    // To use this library you should also depend on the following api() dependencies
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // Implementation dependencies are for our internal use and not exposed in our library jar
    implementation(kotlin("reflect", kotlinVersion))
    implementation("io.github.classgraph:classgraph:$classGraphVersion")
    testImplementation("io.reactivex.rxjava3:rxjava:$rxjavaVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.98".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.96".toBigDecimal()
                }
            }
        }
    }
}
