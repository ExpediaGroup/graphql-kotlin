description = "Federated GraphQL schema generator"

val junitVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-schema-generator"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
}
