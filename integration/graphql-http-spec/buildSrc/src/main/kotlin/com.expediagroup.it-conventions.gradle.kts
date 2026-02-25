import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

tasks {
    kotlin {
        jvmToolchain(17)
    }
    val kotlinJvmVersion: String by project
    withType<KotlinCompile> {
        compilerOptions {
            // intellij gets confused without it
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(kotlinJvmVersion))
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        }
    }
}
