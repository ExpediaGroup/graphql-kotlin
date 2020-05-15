import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.gradle.graphql

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
//    id("com.expediagroup.graphql") version "3.0.0-SNAPSHOT"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-client:3.0.0-SNAPSHOT")
    implementation("io.ktor:ktor-client-okhttp:1.3.1")
    implementation("io.ktor:ktor-client-logging-jvm:1.3.1")
}

// TODO remove this section after final release
buildscript {
    repositories {
        mavenLocal()
    }

    dependencies{
        classpath("com.expediagroup:graphql-kotlin-gradle-plugin:3.0.0-SNAPSHOT")
    }
}

apply(plugin = "com.expediagroup.graphql")

application {
    mainClassName = "com.expediagroup.graphql.examples.client.ApplicationKt"
}

graphql {
    packageName = "com.expediagroup.graphql.generated"
    // you can also use direct sdlEndpoint instead
    endpoint = "http://localhost:8080/graphql"

    // optional
    allowDeprecatedFields = true
    converters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.examples.client.UUIDScalarConverter"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")

        }
    }
}

