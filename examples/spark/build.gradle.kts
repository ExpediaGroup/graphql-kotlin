description = "An Example GraphQL service served by a Spark HTTP server"

plugins {
    id("application")
}

application {
    mainClassName = "com.expediagroup.graphql.examples.spark.Application"
}

val kotlinCoroutinesVersion: String by project
dependencies {
    implementation("com.expediagroup", "graphql-kotlin-schema-generator")
    implementation("com.sparkjava", "spark-core", "2.9.1")
    implementation("log4j", "log4j", "1.2.17")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinCoroutinesVersion)
    testImplementation("org.slf4j", "slf4j-log4j12", "1.7.30")
}
