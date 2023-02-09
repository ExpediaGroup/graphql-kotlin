import com.expediagroup.graphql.plugin.gradle.graphql
import com.expediagroup.it.VerifyGenerateSDLTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-server")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider")
    implementation(libs.kotlin.stdlib)
}

graphql {
  schema {
    packages = listOf("com.example")
  }
}

// integration test
tasks.register<VerifyGenerateSDLTask>("integrationTest") {
    dependsOn("graphqlGenerateSDL")

    actualSchema.set(File(project.buildDir, "schema.graphql"))
    expectedSchema.set(File(rootProject.projectDir, "src/integration/resources/sdl/custom.graphql"))
}
tasks.named("build") {
    dependsOn("integrationTest")
}
