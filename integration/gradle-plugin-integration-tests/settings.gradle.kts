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

// client generator integration tests
include(":client-groovy-extension-it")
include(":client-groovy-task-it")
include(":client-kotlin-extension-it")
include(":client-kotlin-task-it")
include(":client-ktor-jackson-it")
include(":client-ktor-kotlinx-it")
include(":client-webclient-jackson-it")
include(":client-webclient-kotlinx-it")
include(":client-custom-scalars-jackson-it")
include(":client-custom-scalars-kotlinx-it")
include(":client-jacoco-it")
include(":client-polymorphic-types-jackson-it")
include(":client-polymorphic-types-kotlinx-it")
include(":client-skip-include-it")
include(":client-multiple-tasks-it")

project(":client-groovy-extension-it").projectDir = file("client-generator/groovy-extension")
project(":client-groovy-task-it").projectDir = file("client-generator/groovy-task")
project(":client-kotlin-extension-it").projectDir = file("client-generator/kotlin-extension")
project(":client-kotlin-task-it").projectDir = file("client-generator/kotlin-task")
project(":client-ktor-jackson-it").projectDir = file("client-generator/ktor-jackson")
project(":client-ktor-kotlinx-it").projectDir = file("client-generator/ktor-kotlinx")
project(":client-webclient-jackson-it").projectDir = file("client-generator/webclient-jackson")
project(":client-webclient-kotlinx-it").projectDir = file("client-generator/webclient-kotlinx")
project(":client-custom-scalars-jackson-it").projectDir = file("client-generator/custom-scalars-jackson")
project(":client-custom-scalars-kotlinx-it").projectDir = file("client-generator/custom-scalars-kotlinx")
project(":client-jacoco-it").projectDir = file("client-generator/jacoco")
project(":client-polymorphic-types-jackson-it").projectDir = file("client-generator/polymorphic-types-jackson")
project(":client-polymorphic-types-kotlinx-it").projectDir = file("client-generator/polymorphic-types-kotlinx")
project(":client-skip-include-it").projectDir = file("client-generator/skip-include")
project(":client-multiple-tasks-it").projectDir = file("client-generator/multiple-tasks")

// download sdl task integration tests
include(":download-sdl-kotlin-it")
include(":download-sdl-groovy-it")

project(":download-sdl-kotlin-it").projectDir = file("download-sdl/kotlin")
project(":download-sdl-groovy-it").projectDir = file("download-sdl/groovy")

// introspect schema task integration tests
include(":introspection-kotlin-it")
include(":introspection-groovy-it")

project(":introspection-kotlin-it").projectDir = file("introspection/kotlin")
project(":introspection-groovy-it").projectDir = file("introspection/groovy")

// sdl generator integration tests
include(":sdl-extension-kotlin-it")
include(":sdl-extension-groovy-it")
include(":sdl-classpath-hooks-it")
include(":sdl-federated-hooks-it")
include(":sdl-task-kotlin-it")
include(":sdl-task-groovy-it")

project(":sdl-extension-kotlin-it").projectDir = file("sdl/extension-kotlin")
project(":sdl-extension-groovy-it").projectDir = file("sdl/extension-groovy")
project(":sdl-classpath-hooks-it").projectDir = file("sdl/classpath-hooks")
project(":sdl-federated-hooks-it").projectDir = file("sdl/federated-hooks")
project(":sdl-task-kotlin-it").projectDir = file("sdl/task-kotlin")
project(":sdl-task-groovy-it").projectDir = file("sdl/task-groovy")
