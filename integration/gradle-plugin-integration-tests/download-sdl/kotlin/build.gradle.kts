import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLDownloadSDLTask
import kotlin.assert

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
val graphqlDownloadSDL by tasks.getting(GraphQLDownloadSDLTask::class) {
    endpoint.set("http://localhost:$wireMockServerPort/sdl")
    headers.put("X-Custom-Header", "My-Custom-Header-Value")
}

tasks {
    named<Test>("test") {
        dependsOn("graphqlDownloadSDL")
        assert(File(project.buildDir, "schema.graphql").exists())
    }
}
