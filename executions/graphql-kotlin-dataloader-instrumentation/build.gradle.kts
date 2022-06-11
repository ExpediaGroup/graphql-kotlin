description = "Data Loader Instrumentations"

val junitVersion: String by project
val graphQLJavaVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-dataloader"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion") {
        exclude(group = "com.graphql-java", module = "java-dataloader")
    }
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    testImplementation("io.projectreactor:reactor-core:$reactorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.93".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.70".toBigDecimal()
                }
            }
        }
    }
}
