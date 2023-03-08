rootProject.name = "graphql-http-spec"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

// composite graphql-kotlin library build
includeBuild("../..")

include(":spring-server")
include(":ktor-server")
