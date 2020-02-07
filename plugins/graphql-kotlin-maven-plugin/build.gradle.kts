description = "GraphQL Kotlin Maven plugin."

val mavenPluginVersion: String = "3.0"
val mavenProjectVersion: String = "2.2.1"
val mavenPluginAnnotationVersion: String = "3.4"

dependencies {
    api(project(path = ":plugins:graphql-kotlin-plugin-core"))
    implementation("org.apache.maven:maven-plugin-api:$mavenPluginVersion")
    implementation("org.apache.maven:maven-project:$mavenProjectVersion")
    implementation("org.apache.maven.plugin-tools:maven-plugin-annotations:$mavenPluginAnnotationVersion")
}
