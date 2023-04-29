rootProject.name = "graalvm-integration-tests"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

// composite graphql-kotlin library build
includeBuild("../..")

include(":common-graalvm-server")
//include(":spring-graalvm-server")
include(":ktor-graalvm-server")
include(":maven-graalvm-server")
