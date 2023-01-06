description = "GraphQL Kotlin common utilities to generate a client."

val compileTestingVersion: String by project
val graphQLJavaVersion: String by project
val icuVersion: String by project
val jacksonVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val kotlinPoetVersion: String by project
val kotlinxSerializationVersion: String by project
val ktorVersion: String by project
val slf4jVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion") {
        exclude(group = "com.graphql-java", module = "java-dataloader")
    }
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion") {
        exclude("com.fasterxml.jackson.core", "jackson-databind")
        exclude("com.fasterxml.jackson.module", "jackson-module-kotlin")
    }
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation(project(path = ":graphql-kotlin-client-jackson"))
    testImplementation(project(path = ":graphql-kotlin-client-serialization"))
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
    testImplementation("dev.zacsweers.kctfork:core:$compileTestingVersion")
    testImplementation("com.ibm.icu:icu4j:$icuVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
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
