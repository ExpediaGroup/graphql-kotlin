import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

plugins {
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

val serverUrl = System.getenv("wireMockServerUrl") ?: System.getProperty("wireMockServerUrl")
val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("$serverUrl/graphql")
    timeoutConfig.set(com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration(connect = 100, read = 100))
}
