import com.expediagroup.graphql.plugin.gradle.graphql
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.graalvm.native)
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-server")
    implementation(projects.commonGraalvmServer)
    implementation(libs.logback)
    implementation(libs.ktor.server.cio)
    testImplementation(libs.junit.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.ktor.client.content)
    testImplementation(libs.ktor.server.test.host)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.expediagroup.graalvm.ktor.ApplicationKt")
}

graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            verbose.set(true)
            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,ch.qos.logback,org.slf4j")
            buildArgs.add("-H:+ReportExceptionStackTraces")
        }
        metadataRepository {
            enabled.set(true)
        }
    }
}

graphql {
    graalVm {
        packages = listOf("com.expediagroup.graalvm")
    }
}

tasks.register("buildGraalVmNativeImage") {
    dependsOn("build")
    dependsOn("nativeCompile")
}
