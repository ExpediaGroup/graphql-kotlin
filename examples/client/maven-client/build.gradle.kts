import java.time.Duration

description = "Example usage of Maven plugin to generate GraphQL Kotlin Client"

val kotlinJvmVersion: String by project
val kotlinVersion: String by project
val kotlinCoroutinesVersion: String by project
val reactorVersion: String by project

repositories {
    mavenCentral()
    jcenter()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-client")
}

tasks {
    /* Gradle is used to invoke maven wrapper */
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.version,
        "kotlinJvmTarget" to kotlinJvmVersion,
        "kotlinVersion" to kotlinVersion,
        "kotlinCoroutinesVersion" to kotlinCoroutinesVersion,
        "reactorVersion" to reactorVersion
    )
    val wireMockServerPort: Int? = ext.get("wireMockServerPort") as? Int
    val mavenBuild by register("mavenBuild") {
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
