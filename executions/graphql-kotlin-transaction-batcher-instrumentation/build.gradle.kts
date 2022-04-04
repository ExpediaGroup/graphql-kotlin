description = "Transaction Batcher Instrumentation"

val junitVersion: String by project
val graphQLJavaVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-server"))
    api("com.graphql-java:graphql-java:$graphQLJavaVersion")
    testImplementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorExtensionsVersion")
    testImplementation("io.projectreactor:reactor-core:$reactorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
