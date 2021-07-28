plugins {
    id("com.android.application")
    id("com.expediagroup.graphql")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    compileSdkVersion(30)
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "5.0.0-SNAPSHOT"
val kotlinVersion = System.getenv("KOTLIN_VERSION") ?: "1.5.21"
dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphQLKotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
}

graphql {
    client {
        schemaFileName = "${project.projectDir}/schema.graphql"
        packageName = "com.expediagroup.android.generated"
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}
