import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: re
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
val graphqlIntrospectSchema by tasks.getting(GraphQLIntrospectSchemaTask::class) {
    endpoint.set("http://localhost:$wireMockServerPort/graphql")
    headers.put("X-Custom-Header", "My-Custom-Header-Value")
}

tasks {
    named<Test>("test") {
        dependsOn("graphqlIntrospectSchema")

        doLast {
            if (!File(project.buildDir, "schema.graphql").exists()) {
                throw RuntimeException("failed to introspect the schema and generate schema.graphql file")
            }
        }
    }
}
