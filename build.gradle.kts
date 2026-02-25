import java.time.Duration

description = "Libraries for running a GraphQL server in Kotlin"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

plugins {
    alias(libs.plugins.nexus.publish)
}

allprojects {
    buildscript {
        repositories {
            mavenCentral()
            mavenLocal {
                content {
                    includeGroup("com.expediagroup")
                }
            }
        }
    }

    repositories {
        mavenCentral()
        google()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }
}

tasks {
    nexusPublishing {
        repositories {
            sonatype {
                username.set(System.getenv("SONATYPE_USERNAME"))
                password.set(System.getenv("SONATYPE_PASSWORD"))
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            }
        }

        transitionCheckOptions {
            maxRetries.set(60)
            delayBetween.set(Duration.ofMillis(5000))
        }
    }

    register("resolveIntegrationTestDependencies") {
        // our Gradle and Maven integration tests run in separate VMs that will need access to the generated artifacts
        // we will need to run them after artifacts are published to local m2 repo
        for (graphQLKotlinProject in project.childProjects) {
            dependsOn(":${graphQLKotlinProject.key}:publishToMavenLocal")
        }
    }
}
