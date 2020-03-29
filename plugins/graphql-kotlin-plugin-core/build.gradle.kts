description = "GraphQL Kotlin common plugin utilities library."

val ktorVersion: String by project
val kotlinPoetVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api(project(path = ":graphql-kotlin-client"))
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.26.2")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.8".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.65".toBigDecimal()
                }
            }
        }
    }
}
