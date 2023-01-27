import java.util.Properties
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
}

// this is a workaround to enable version catalog usage in the convention plugin
// see https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()
val properties = Properties()
properties.load(File(rootDir.parent, "gradle.properties").inputStream())
for ((key, value) in properties) {
    project.ext[key.toString()] = value
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.icu)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks {
    detekt {
        toolVersion = libs.versions.detekt.get()
        config = files(File(rootDir.parent, "detekt.yml").absolutePath)
    }
    kotlin {
        jvmToolchain(17)
    }
    ktlint {
        version.set(libs.versions.ktlint.core.get())
    }
    jar {
        enabled = false
    }
    test {
        useJUnitPlatform()
    }
}
