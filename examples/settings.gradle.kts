dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))

            // examples specific libs
            library("ktor-client-jvm-logging", "io.ktor", "ktor-client-logging-jvm").versionRef("ktor")
            library("spring-boot-validation", "org.springframework.boot", "spring-boot-starter-validation").versionRef("spring-boot")
        }
    }
}

rootProject.name = "graphql-kotlin-examples"

// composite builds
includeBuild("..")

// client examples
include(":client-examples")
include(":client-examples:gradle-client-example")
include(":client-examples:maven-client-example")
include(":client-examples:server-client-example")

// federation examples
include(":federation-products-subgraph")
include(":federation-reviews-subgraph")

// server examples
include(":ktor-server")
include(":spring-server")

// project mappings
project(":client-examples").projectDir = file("client")
project(":client-examples:gradle-client-example").projectDir = file("client/gradle-client")
project(":client-examples:maven-client-example").projectDir = file("client/maven-client")
project(":client-examples:server-client-example").projectDir = file("client/server")

project(":federation-products-subgraph").projectDir = file("federation/products-subgraph")
project(":federation-reviews-subgraph").projectDir = file("federation/reviews-subgraph")

project(":spring-server").projectDir = file("server/spring-server")
project(":ktor-server").projectDir = file("server/ktor-server")
