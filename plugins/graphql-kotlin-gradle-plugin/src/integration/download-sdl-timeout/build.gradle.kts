import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

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
val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("$serverUrl/sdl")
    timeoutConfig.set(TimeoutConfiguration(connect = 100, read = 100))
}
