description = "Federated GraphQL schema generator"

val junitVersion: String by project
val federationGraphQLVersion: String by project

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api("com.apollographql.federation:federation-graphql-java-support:$federationGraphQLVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}

tasks {
    // Relocate the dependency so that users don't have to require jcenter
    shadowJar {
        minimize()
        archiveClassifier.set("")
        configurations = mutableListOf(project.configurations.compileClasspath.get())
        relocate("com.apollographql.federation", "shadow.com.apollographql.federation")
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.96".toBigDecimal()
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
