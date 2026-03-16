plugins {
    id("com.android.application")
    id("com.expediagroup.graphql")
}

apply(plugin = "org.jetbrains.kotlin.android")
apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

android {
    compileSdk = 30
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    namespace = "com.expediagroup.graphqlkotlin"
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client")
    implementation(libs.kotlin.stdlib)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/schema.graphql")
        packageName = "com.expediagroup.android.generated"
        serializer = com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer.KOTLINX
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
}

