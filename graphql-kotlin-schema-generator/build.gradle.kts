description = "Code-only GraphQL schema generation for Kotlin"

val classGraphVersion: String by project
val graphQLJavaVersion: String by project
val jacksonVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project

dependencies {
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    // TODO change below from api to implementation?
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
    api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    api("io.github.classgraph:classgraph:$classGraphVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    testImplementation("io.reactivex.rxjava2:rxjava:2.2.14")
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
