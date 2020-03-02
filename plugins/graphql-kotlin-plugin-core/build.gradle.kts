description = "GraphQL Kotlin common plugin utilities library."

val ktorVersion: String by project
val kotlinPoetVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    api("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
}
