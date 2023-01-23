rootProject.name = "gradle-plugin-integration-tests"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

// composite graphql-kotlin library build
includeBuild("../..")

include(":client-custom-scalars-jackson-it")
include(":client-custom-scalars-kotlinx-it")
include(":client-jacoco-it")
include(":client-polymorphic-types-jackson-it")
include(":client-polymorphic-types-kotlinx-it")
include(":client-skip-include-it")

// remap directories to projects
project(":client-custom-scalars-jackson-it").projectDir = file("client-generator/custom-scalars-jackson")
project(":client-custom-scalars-kotlinx-it").projectDir = file("client-generator/custom-scalars-kotlinx")
project(":client-jacoco-it").projectDir = file("client-generator/jacoco")
project(":client-polymorphic-types-jackson-it").projectDir = file("client-generator/polymorphic-types-jackson")
project(":client-polymorphic-types-kotlinx-it").projectDir = file("client-generator/polymorphic-types-kotlinx")
project(":client-skip-include-it").projectDir = file("client-generator/skip-include")

// sdl generator integration tests
// TODO
