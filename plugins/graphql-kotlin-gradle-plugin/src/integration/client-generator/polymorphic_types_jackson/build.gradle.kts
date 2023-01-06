import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

buildscript {
    repositories {
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }

    val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "7.0.0-SNAPSHOT"
    dependencies {
        classpath("com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion")
    }
}

plugins {
    id("org.springframework.boot") version "2.7.5"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
}

apply(plugin = "com.expediagroup.graphql")

group = "com.expediagroup"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "7.0.0-SNAPSHOT"
val junitVersion = System.getenv("JUNIT_VERSION") ?: "5.8.2"
val kotlinVersion = System.getenv("KOTLIN_VERSION") ?: "1.7.21"
val springBootVersion = System.getenv("SPRINGBOOT_VERSION") ?: "2.7.5"
dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client:$graphQLKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphQLKotlinVersion")
    testImplementation(kotlin("test-junit5", kotlinVersion))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.polymorphic"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.polymorphic.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
}
