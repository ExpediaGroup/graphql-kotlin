import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration
import java.time.Instant

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    jacoco
    `java-library`
    signing
    `maven-publish`
}

// this is a workaround to enable version catalog usage in the convention plugin
// see https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()
tasks {
    kotlin {
        jvmToolchain(17)
    }
    val kotlinJvmVersion: String by project
    withType<KotlinCompile> {
        kotlinOptions {
            // intellij gets confused without it
            jvmTarget = kotlinJvmVersion
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    check {
        dependsOn(jacocoTestCoverageVerification)
    }
    detekt {
        toolVersion = libs.versions.detekt.get()
        config = files("${rootProject.projectDir}/detekt.yml")
    }
    ktlint {
        version.set(libs.versions.ktlint.core.get())
    }
    jacoco {
        toolVersion = libs.versions.jacoco.get()
    }
    jar {
        manifest {
            attributes["Built-By"] = "Expedia Group"
            attributes["Build-Jdk"] = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})"
            attributes["Build-Timestamp"] = Instant.now().toString()
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Implementation-Title"] = project.name
            attributes["Implementation-Version"] = project.version
        }

        // NOTE: in order to run gradle and maven plugin integration tests we need to have our build artifacts available in local repo
        finalizedBy("publishToMavenLocal")
    }

    // published sources and javadoc artifacts
    val jarComponent = project.components.getByName("java")
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
                    name.set("${project.group}:${project.name}")
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
                        mavenPom.description.set(project.description)
                    }
                }
            }
            // com.gradle.plugin-publish creates publication automatically
            if (project.name != "graphql-kotlin-gradle-plugin") {
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

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.jdk8)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.mockk)
}
