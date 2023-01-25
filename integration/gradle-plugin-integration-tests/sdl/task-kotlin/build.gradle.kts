import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.it.VerifyGenerateSDLTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    implementation(libs.kotlin.stdlib)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.example"))
}

// integration test
tasks.register<VerifyGenerateSDLTask>("integrationTest") {
    dependsOn("graphqlGenerateSDL")

    actualSchema.set(File(project.buildDir, "schema.graphql"))
    expectedSchema.set(File(rootProject.projectDir, "src/integration/resources/sdl/schema.graphql"))
}
tasks.named("build") {
    dependsOn("integrationTest")
}
