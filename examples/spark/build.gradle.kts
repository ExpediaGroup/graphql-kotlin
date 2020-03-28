description = "An Example GraphQL service served by a Spark HTTP server"

plugins {
    id("application")
}

application {
    mainClassName = "com.expediagroup.graphql.examples.spark.Application"
}

dependencies {
    compile("com.sparkjava", "spark-core", "2.9.1")
    compile("log4j", "log4j", "1.2.17")
    testCompile("org.slf4j", "slf4j-log4j12", "1.7.30")
}
