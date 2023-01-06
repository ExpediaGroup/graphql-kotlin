import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
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
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.serialization") version "1.7.21"
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
val ktorVersion = System.getenv("KTOR_VERSION") ?: "2.0.0"
val logbackVersion = System.getenv("LOGBACK_VERSION") ?: "1.2.1"
dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.expediagroup:graphql-kotlin-ktor-client:$graphQLKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-server:$graphQLKotlinVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation(kotlin("test-junit5", kotlinVersion))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
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

val graphqlGenerateSDL by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.polymorphic"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.polymorphic.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    serializer.set(GraphQLSerializer.KOTLINX)
}
