import java.time.Duration

description = "Example Maven project that uses GraphQL Kotlin Client and Maven Plugin"

dependencies {
    implementation("com.expediagroup:graphql-kotlin-client")
    implementation("com.expediagroup:graphql-kotlin-maven-plugin")
}

tasks {
    val copyDependencies by register<Copy>("copyDependencies") {

        from(configurations.runtimeClasspath) {
            // we only need to explicitly copy graphql-kotlin libraries as they won't be available in m2 repository at this point
            // we could copy other direct dependencies as well but it looks like it might not work for maven-plugin-plugin as it relies on some transitive dependencies to run
            include("graphql-kotlin*")
        }
        into("${project.buildDir}/dependencies")
    }
    val installPlugin by register("installMavenPlugin") {
        dependsOn("copyDependencies")
        timeout.set(Duration.ofSeconds(60))
        doLast {
            exec {
                // install plugin to local m2 repository
                val mavenPluginJar = "${project.buildDir}/dependencies/graphql-kotlin-maven-plugin-${project.version}.jar"
                commandLine("${project.projectDir}/mvnw",
                    "install:install-file",
                    "-Dfile=$mavenPluginJar",
                    "-DgroupId=com.expediagroup",
                    "-DartifactId=graphql-kotlin-maven-plugin",
                    "-Dversion=$project.version",
                    "-Dpackaging=maven-plugin")
            }
        }
    }
    val buildMaven by register("buildMaven") {
        dependsOn("installMavenPlugin")
        timeout.set(Duration.ofSeconds(60))
        doLast {
            exec {
                val kotlinVersion: String by project
                val kotlinCoroutinesVersion: String by project
                environment("graphql-kotlin.version", project.version)
                environment("kotlin.version", kotlinVersion)
                environment("kotlin-coroutines.version", kotlinCoroutinesVersion)

                commandLine("${project.projectDir}/mvnw", "clean", "package")
            }
        }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
        dependsOn("buildMaven")
    }
}
