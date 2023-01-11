description = "Module containing SchemaGeneratorHooksProvider Service Provider Interface (SPI)"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    implementation(projects.graphqlKotlinSchemaGenerator)
}
