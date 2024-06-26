plugins {
    id("com.android.application")
    id("com.expediagroup.graphql")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    compileSdk = 30
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "com.expediagroup.graphqlkotlin"
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client")
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/schema.graphql")
        packageName = "com.expediagroup.android.generated"
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}
