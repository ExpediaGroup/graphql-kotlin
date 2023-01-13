import java.time.Duration

description = "Example usage of Maven plugin to generate GraphQL Kotlin Client"

plugins {
    id("com.expediagroup.graphql.examples.conventions")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-client")
}

tasks {
    val kotlinJvmVersion: String by project
    /* Gradle is used to invoke maven wrapper */
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.version,
        "kotlinJvmTarget" to kotlinJvmVersion,
        "kotlinVersion" to libs.versions.kotlin.get(),
        "kotlinxCoroutinesVersion" to libs.versions.kotlinx.coroutines.get(),
        "reactorVersion" to libs.versions.reactor.core.get()
    )
    val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
    val mavenBuild by register("mavenBuild") {
        dependsOn(gradle.includedBuild("graphql-kotlin").task(":resolveIntegrationTestDependencies"))
        timeout.set(Duration.ofSeconds(500))
        doLast {
            exec {
                environment(mavenEnvironmentVariables)
                environment("graphqlEndpoint", "http://localhost:$wireMockServerPort/sdl")
                commandLine("${project.projectDir}/mvnw", "clean", "verify", "--no-transfer-progress")
            }
        }
    }
    check {
        dependsOn(mavenBuild.path)
    }
}

// below configuration added so it compiles nicely in intellij
sourceSets {
    findByName("main")?.java?.srcDir("${project.projectDir}/target/generated-sources/graphql")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn("mavenBuild")
}
ktlint {
    filter {
        exclude("**/generated/**")
    }
}
