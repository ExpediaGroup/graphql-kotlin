import com.expediagroup.graphql.plugin.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
import com.expediagroup.graphql.plugin.gradle.graphql

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
    id("com.expediagroup.graphql") version "4.0.0-alpha.1"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client:4.0.0-alpha.1")
    implementation("io.ktor:ktor-client-okhttp:1.3.1")
    implementation("io.ktor:ktor-client-logging-jvm:1.3.1")
}

application {
    mainClassName = "com.expediagroup.graphql.examples.client.ApplicationKt"
}

graphql {
    client {
        packageName = "com.expediagroup.graphql.generated"
        // you can also use direct sdlEndpoint instead
        endpoint = "http://localhost:8080/graphql"

        // optional
        allowDeprecatedFields = true
        headers["X-Custom-Header"] = "My-Custom-Header"
        converters["UUID"] = ScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.examples.client.UUIDScalarConverter")
        clientType = GraphQLClientType.KTOR
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")

        }
    }
}

