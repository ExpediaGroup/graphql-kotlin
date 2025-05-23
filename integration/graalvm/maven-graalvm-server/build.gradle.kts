import java.time.Duration

plugins {
    alias(libs.plugins.kotlin.jvm)
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
    val kotlinJvmVersion: String by project
    /*
    integration test is run by invoking maven directly
     */
    val mavenEnvironmentVariables = mapOf(
        "graphqlKotlinVersion" to project.ext["version"],
        "graphqlJavaVersion" to libs.versions.graphql.java.get(),
        "junitVersion" to libs.versions.junit.asProvider().get(),
        "kotlinJvmTarget" to kotlinJvmVersion,
        "kotlinVersion" to libs.versions.kotlin.get(),
        "ktorVersion" to libs.versions.ktor.get(),
        "logbackVersion" to libs.versions.logback.get(),
        "nativeMavenPluginVersion" to libs.versions.graalvm.get()
    )

    // copy native image to the same path as gradle builds to simplify testing
    val copyNativeImage by register<Copy>("copyNativeImage") {
        from("${project.projectDir}/target/maven-graalvm-server")
        into("${project.buildDir}/native/nativeCompile")
    }
    val buildGraalVmNativeImage by register("buildGraalVmNativeImage") {
        dependsOn(gradle.includedBuild("graphql-kotlin").task(":resolveIntegrationTestDependencies"))
        dependsOn(":common-graalvm-server:publishToMavenLocal")
        timeout.set(Duration.ofSeconds(1200))
        doLast {
            exec {
                environment(mavenEnvironmentVariables)
                commandLine("${project.projectDir}/mvnw", "-Pnative", "clean", "package", "--no-transfer-progress")
            }
        }
        finalizedBy(copyNativeImage.path)
    }
    check {
        dependsOn(buildGraalVmNativeImage.path)
    }
    clean {
        delete("target")
    }
}
