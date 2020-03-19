
import de.marcphilipp.gradle.nexus.NexusPublishExtension
import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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
    id("de.marcphilipp.nexus-publish")
    id("io.codearte.nexus-staging")
}

allprojects {
    buildscript {
        repositories {
            mavenLocal()
            jcenter()
            mavenCentral()
        }
    }

    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
    }

    apply(plugin = "de.marcphilipp.nexus-publish")

    configure<NexusPublishExtension> {
        repositories {
            sonatype {
                username.set(System.getenv("SONATYPE_USERNAME"))
                password.set(System.getenv("SONATYPE_PASSWORD"))
            }
        }
    }
}

subprojects {
    val kotlinVersion: String by project
    val junitVersion: String by project
    val mockkVersion: String by project

    val detektVersion: String by project
    val ktlintVersion: String by project
    val jacocoVersion: String by project

    val currentProject = this

    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    tasks {
        check {
            dependsOn(jacocoTestCoverageVerification)
        }
        detekt {
            toolVersion = detektVersion
            config = files("${rootProject.projectDir}/detekt.yml")
        }
        ktlint {
            version.set(ktlintVersion)
        }
        jacoco {
            toolVersion = jacocoVersion
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
        }
        test {
            useJUnitPlatform()
            finalizedBy(jacocoTestReport)
        }

        // published artifacts
        val jarComponent = currentProject.components.getByName("java")
        val sourcesJar by registering(Jar::class) {
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }
        val dokka by getting(DokkaTask::class) {
            outputFormat = "javadoc"
            outputDirectory = "$buildDir/javadoc"
        }
        val javadocJar by registering(Jar::class) {
            archiveClassifier.set("javadoc")
            from("$buildDir/javadoc")
            dependsOn(dokka.path)
        }
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
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
                    from(jarComponent)
                    artifact(sourcesJar.get())
                    artifact(javadocJar.get())
                }
            }
        }
        signing {
            setRequired {
                (rootProject.extra["isReleaseVersion"] as Boolean) && gradle.taskGraph.hasTask("publish")
            }
            val signingKey: String? = System.getenv("GPG_SECRET")
            val signingPassword: String? = System.getenv("GPG_PASSPHRASE")
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publishing.publications["mavenJava"])
        }
    }

    dependencies {
        implementation(kotlin("stdlib", kotlinVersion))
        testImplementation(kotlin("test", kotlinVersion))
        testImplementation(kotlin("test-junit5", kotlinVersion))
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")
    }
}

tasks {
    jar {
        enabled = false
    }
    nexusStaging {
        username = System.getenv("SONATYPE_USERNAME")
        password = System.getenv("SONATYPE_PASSWORD")
        packageGroup = rootProject.group.toString()
        numberOfRetries = 60
        delayBetweenRetriesInMillis = 5000
    }

    val publish by getting
    publish.dependsOn(":initializeSonatypeStagingRepository")
}
