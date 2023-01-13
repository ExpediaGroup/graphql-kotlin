description = "Default federated GraphQL schema generator hooks provider"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    implementation(projects.graphqlKotlinFederation)
    implementation(projects.graphqlKotlinHooksProvider)
}
