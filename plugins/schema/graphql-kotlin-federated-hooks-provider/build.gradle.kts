description = "Default federated GraphQL schema generator hooks provider"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    implementation(project(":graphql-kotlin-federation"))
    implementation(project(":graphql-kotlin-hooks-provider"))
}
