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
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
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
