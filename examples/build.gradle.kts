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
}

subprojects {
    val properties = Properties()
    properties.load(File(rootDir.parent, "gradle.properties").inputStream())
    for ((key, value) in properties) {
        this.ext[key.toString()] = value
    }

    val kotlinVersion: String by project
    val junitVersion: String by project

    val detektVersion: String by project
    val ktlintVersion: String by project

    apply(plugin = "kotlin")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencies {
        implementation(kotlin("stdlib", kotlinVersion))
        implementation("com.expediagroup:graphql-kotlin-spring-server")
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
