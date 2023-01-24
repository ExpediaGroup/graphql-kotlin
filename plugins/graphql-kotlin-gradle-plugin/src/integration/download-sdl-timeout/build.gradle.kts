import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.config.TimeoutConfiguration
import com.expediagroup.graphql.plugin.gradle.graphql
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

plugins {
  kotlin("jvm") version "1.7.21"
  
  id("com.expediagroup.graphql")
  application
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
  implementation("com.expediagroup:graphql-kotlin-spring-client:7.0.0-SNAPSHOT")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
  endpoint.set("http://localhost:55208/sdl")
  timeoutConfig.set(TimeoutConfiguration(connect = 100, read = 100))
}