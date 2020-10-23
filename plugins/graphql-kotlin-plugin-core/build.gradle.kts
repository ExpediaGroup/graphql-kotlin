description = "GraphQL Kotlin common plugin utilities library."

val graphQLJavaVersion: String by project
val kotlinPoetVersion: String by project
val ktorVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-ktor-client"))
    api(project(path = ":graphql-kotlin-spring-client"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.90".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
            }
        }
    }
}
