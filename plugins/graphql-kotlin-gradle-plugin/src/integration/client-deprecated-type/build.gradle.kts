import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

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

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated")
    schemaFile.set(file("${project.projectDir}/schema.graphql"))
}
