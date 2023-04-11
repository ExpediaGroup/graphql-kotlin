rootProject.name = "graalvm-integration-tests"

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

//include(":spring-graalvm-server")
include(":ktor-graalvm-server")
