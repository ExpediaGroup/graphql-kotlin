import com.expediagroup.graphql.plugin.gradle.graphql

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

tasks {
    test {
        useJUnitPlatform()
    }
}
application {
    mainClass.set("com.expediagroup.graalvm.ktor.ApplicationKt")
}

graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            verbose.set(true)
            buildArgs.add("--initialize-at-build-time=io.ktor,kotlin,kotlinx.io,ch.qos.logback,org.slf4j")
            buildArgs.add("-H:+ReportExceptionStackTraces")
            jvmArgs("-Xmx6g")
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
