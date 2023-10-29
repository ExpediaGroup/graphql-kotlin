description = "GraphQL Kotlin Maven Plugin that can generate type-safe GraphQL Kotlin client and GraphQL schema in SDL format using reflections"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.maven.plugin.development)
}

dependencies {
    api(projects.graphqlKotlinClientGenerator)
    api(projects.graphqlKotlinSdlGenerator)
    api(projects.graphqlKotlinGraalvmMetadataGenerator)
    api(libs.kotlinx.coroutines.core)
    implementation(libs.maven.plugin.annotations)
    implementation(libs.maven.plugin.api)
    implementation(libs.maven.project)
}

tasks {
    publishing {
        publications {
            val mavenPublication = findByName("mavenJava") as? MavenPublication
            mavenPublication?.pom {
                packaging = "maven-plugin"
            }
        }
    }
}
