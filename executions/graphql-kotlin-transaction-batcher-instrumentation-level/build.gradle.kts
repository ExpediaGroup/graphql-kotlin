description = "Transaction Batcher Instrumentation by level"

val junitVersion: String by project
val graphQLJavaVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project
val slf4jVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-transaction-batcher"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    testImplementation("io.projectreactor:reactor-core:$reactorVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
