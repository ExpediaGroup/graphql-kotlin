import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration
import java.time.Instant

description = "Libraries for running a GraphQL server in Kotlin"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") apply false
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    jacoco
    signing
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin")
}

allprojects {
    buildscript {
        repositories {
            mavenCentral()
            mavenLocal()
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

subprojects {
    val kotlinJvmVersion: String by project

    val currentProject = this

    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = kotlinJvmVersion
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }

        check {
            dependsOn(jacocoTestCoverageVerification)
        }
        detekt {
            toolVersion = rootProject.project.libs.versions.detekt.get()
            config = files("${rootProject.projectDir}/detekt.yml")
        }
        ktlint {
            version.set(rootProject.project.libs.versions.ktlint.core.get())
        }
        jacoco {
            toolVersion = rootProject.project.libs.versions.jacoco.get()
        }
        jar {
            manifest {
                attributes["Built-By"] = "Expedia Group"
                attributes["Build-Jdk"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
                attributes["Build-Timestamp"] = Instant.now().toString()
                attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
                attributes["Implementation-Title"] = currentProject.name
                attributes["Implementation-Version"] = project.version
            }

            // NOTE: in order to run gradle and maven plugin integration tests we need to have our build artifacts available in local repo
            finalizedBy("publishToMavenLocal")
        }
        java {
            // even though we don't have any Java code, since we are building using Java LTS version,
            // this is required for Gradle to set the correct JVM versions in the module metadata
            targetCompatibility = JavaVersion.VERSION_1_8
            sourceCompatibility = JavaVersion.VERSION_1_8
        }

        // published sources and javadoc artifacts
        val jarComponent = currentProject.components.getByName("java")
        val sourcesJar by registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        val dokka = named("dokkaJavadoc", DokkaTask::class)
        val javadocJar by registering(Jar::class) {
            archiveClassifier.set("javadoc")
            from("$buildDir/dokka/javadoc")
            dependsOn(dokka)
        }
        publishing {
            publications {
                withType<MavenPublication> {
                    pom {
                        name.set("${currentProject.group}:${currentProject.name}")
                        url.set("https://github.com/ExpediaGroup/graphql-kotlin")
                        licenses {
                            license {
                                name.set("The Apache Software License, Version 2.0")
                                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        organization {
                            name.set("Expedia Group")
                            name.set("https://www.expediagroup.com/")
                        }
                        developers {
                            developer {
                                name.set("Expedia Group Committers")
                                email.set("oss@expediagroup.com")
                                organization.set("Expedia Group")
                                organizationUrl.set("https://www.expediagroup.com/")
                            }
                        }
                        scm {
                            connection.set("scm:git:git://github.com/ExpediaGroup/graphql-kotlin.git")
                            developerConnection.set("scm:git:git://github.com/ExpediaGroup/graphql-kotlin.git")
                            url.set("https://github.com/ExpediaGroup/graphql-kotlin")
                        }

                        // child projects need to be evaluated before their description can be read
                        val mavenPom = this
                        afterEvaluate {
                            mavenPom.description.set(currentProject.description)
                        }
                    }
                }
                // com.gradle.plugin-publish creates publication automatically
                if (currentProject.name != "graphql-kotlin-gradle-plugin") {
                    create<MavenPublication>("mavenJava") {
                        from(jarComponent)
                        artifact(sourcesJar.get())
                        // no need to publish javadocs for SNAPSHOT builds
                        if (rootProject.extra["isReleaseVersion"] as Boolean) {
                            artifact(javadocJar.get())
                        }
                    }
                }
            }
        }
        signing {
            setRequired {
                (rootProject.extra["isReleaseVersion"] as Boolean) && (gradle.taskGraph.hasTask("publish") || gradle.taskGraph.hasTask("publishPlugins"))
            }
            val signingKey: String? = System.getenv("GPG_SECRET")
            val signingPassword: String? = System.getenv("GPG_PASSPHRASE")
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications)
        }

        test {
            useJUnitPlatform()
            finalizedBy(jacocoTestReport)
        }
    }

    // typesafe accessors to version catalog do not work in the subprojects/allprojects block, need to use rootProject.project
    dependencies {
        implementation(rootProject.project.libs.kotlin.stdlib)
        implementation(rootProject.project.libs.kotlin.reflect)
        implementation(rootProject.project.libs.kotlinx.coroutines.jdk8)
        testImplementation(rootProject.project.libs.kotlin.test)
        testImplementation(rootProject.project.libs.kotlin.junit.test)
        testImplementation(rootProject.project.libs.junit.api)
        testImplementation(rootProject.project.libs.junit.engine)
        testImplementation(rootProject.project.libs.mockk)
    }
}

tasks {
    jar {
        enabled = false
    }
    nexusPublishing {
        repositories {
            sonatype {
                username.set(System.getenv("SONATYPE_USERNAME"))
                password.set(System.getenv("SONATYPE_PASSWORD"))
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
