import io.gitlab.arturbosch.detekt.detekt
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

    val kotlinVersion: String by project
    val junitVersion: String by project
    val kotlinCoroutinesVersion: String by project

    val detektVersion: String by project
    val ktlintVersion: String by project

    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencies {
        implementation(kotlin("stdlib", kotlinVersion))
        implementation(kotlin("reflect", kotlinVersion))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinCoroutinesVersion")
        implementation("com.ibm.icu:icu4j:69.1")
        testImplementation(kotlin("test-junit5", kotlinVersion))
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    tasks {
        detekt {
            toolVersion = detektVersion
            config = files(File(rootDir.parent, "detekt.yml").absolutePath)
        }
        ktlint {
            version.set(ktlintVersion)
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
