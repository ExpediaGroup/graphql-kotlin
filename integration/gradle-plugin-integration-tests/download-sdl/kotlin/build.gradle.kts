import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:$wireMockServerPort/sdl")
    headers.put("X-Custom-Header", "My-Custom-Header-Value")
}

tasks {
    named<Test>("test") {
        dependsOn("graphqlDownloadSDL")

        doLast {
            if (!File(project.buildDir, "schema.graphql").exists()) {
                throw RuntimeException("failed to download schema.graphql file")
            }
        }
    }
}
