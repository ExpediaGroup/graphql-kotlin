import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

plugins {
  kotlin("jvm") version "1.7.21"
  id("com.expediagroup.graphql")
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
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.21")
  implementation("com.expediagroup:graphql-kotlin-spring-client")
}

val serverUrl = System.getenv("wireMockServerUrl") ?: System.getProperty("wireMockServerUrl")
val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
  endpoint.set("$serverUrl/graphql")
  timeoutConfig.set(com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration(connect = 100, read = 100))
}
