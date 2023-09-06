import com.expediagroup.graphql.plugin.gradle.graphql

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    implementation(projects.commonGraalvmServer)
    testImplementation(libs.junit.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.spring.boot.test)
}

tasks {
    kotlin {
        jvmToolchain(17)
    }
    test {
        useJUnitPlatform()
    }
}

graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            verbose.set(true)
        }
        metadataRepository {
            enabled.set(true)
        }
    }
}

graphql {
    graalVm {
        packages = listOf("com.expediagroup.graalvm")
        mainClassName = "com.expediagroup.graalvm.spring.ApplicationKt"
    }
}

tasks.register("buildGraalVmNativeImage") {
    dependsOn("build")
    dependsOn("nativeCompile")
}
