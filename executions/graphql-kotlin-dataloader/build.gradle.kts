description = "Graphql Kotlin Data Loader"

val junitVersion: String by project
val graphQLJavaDataLoaderVersion: String by project
val reactorVersion: String by project
val reactorExtensionsVersion: String by project

dependencies {
    api("com.graphql-java:java-dataloader:$graphQLJavaDataLoaderVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}
