import java.util.Properties

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
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
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }
}

subprojects {
    val properties = Properties()
    properties.load(File(rootDir.parent, "gradle.properties").inputStream())
    for ((key, value) in properties) {
        this.ext[key.toString()] = value
    }


    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencies {
        implementation(rootProject.project.libs.kotlin.stdlib)
        implementation(rootProject.project.libs.kotlin.reflect)
        implementation(rootProject.project.libs.kotlinx.coroutines.jdk8)
        implementation(rootProject.project.libs.icu)
        testImplementation(rootProject.project.libs.kotlin.junit.test)
        testImplementation(rootProject.project.libs.junit.api)
        testImplementation(rootProject.project.libs.junit.engine)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    tasks {
        detekt {
            toolVersion = rootProject.project.libs.versions.detekt.get()
            config = files(File(rootDir.parent, "detekt.yml").absolutePath)
        }
        ktlint {
            version.set(rootProject.project.libs.versions.ktlint.core.get())
        }
        jar {
            enabled = false
        }
        test {
            useJUnitPlatform()
        }
    }
}

tasks {
    jar {
        enabled = false
    }
}
