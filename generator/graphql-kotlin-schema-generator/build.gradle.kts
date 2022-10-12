description = "Code-only GraphQL schema generation for Kotlin"

val classGraphVersion: String by project
val graphQLJavaVersion: String by project
val kotlinxCoroutinesVersion: String by project
val rxjavaVersion: String by project
val junitVersion: String by project
val slf4jVersion: String by project

dependencies {
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinxCoroutinesVersion")
    implementation("io.github.classgraph:classgraph:$classGraphVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation("io.reactivex.rxjava3:rxjava:$rxjavaVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
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
                    minimum = "0.93".toBigDecimal()
                }
            }
        }
    }
}
