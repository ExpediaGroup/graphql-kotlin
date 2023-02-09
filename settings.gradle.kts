rootProject.name = "graphql-kotlin"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Clients
include(":graphql-kotlin-client")
include(":graphql-kotlin-client-serialization")
include(":graphql-kotlin-client-jackson")
include(":graphql-kotlin-ktor-client")
include(":graphql-kotlin-spring-client")

// Generator
include(":graphql-kotlin-schema-generator")
include(":graphql-kotlin-federation")

// Plugins
include(":graphql-kotlin-gradle-plugin")
include(":graphql-kotlin-maven-plugin")
include(":graphql-kotlin-client-generator")
include(":graphql-kotlin-sdl-generator")
include(":graphql-kotlin-hooks-provider")
include(":graphql-kotlin-federated-hooks-provider")

// Servers
include(":graphql-kotlin-server")
include(":graphql-kotlin-spring-server")
include(":graphql-kotlin-ktor-server")

// Executions
include(":graphql-kotlin-dataloader")
include(":graphql-kotlin-dataloader-instrumentation")
include(":graphql-kotlin-automatic-persisted-queries")

//
// Project mappings so we don't need to create projects that group subprojects
//

// Clients
project(":graphql-kotlin-client").projectDir = file("clients/graphql-kotlin-client")
project(":graphql-kotlin-client-serialization").projectDir = file("clients/graphql-kotlin-client-serialization")
project(":graphql-kotlin-client-jackson").projectDir = file("clients/graphql-kotlin-client-jackson")
project(":graphql-kotlin-ktor-client").projectDir = file("clients/graphql-kotlin-ktor-client")
project(":graphql-kotlin-spring-client").projectDir = file("clients/graphql-kotlin-spring-client")

// Generator
project(":graphql-kotlin-schema-generator").projectDir = file("generator/graphql-kotlin-schema-generator")
project(":graphql-kotlin-federation").projectDir = file("generator/graphql-kotlin-federation")

// Plugins
project(":graphql-kotlin-gradle-plugin").projectDir = file("plugins/graphql-kotlin-gradle-plugin")
project(":graphql-kotlin-maven-plugin").projectDir = file("plugins/graphql-kotlin-maven-plugin")
project(":graphql-kotlin-client-generator").projectDir = file("plugins/client/graphql-kotlin-client-generator")
project(":graphql-kotlin-sdl-generator").projectDir = file("plugins/schema/graphql-kotlin-sdl-generator")
project(":graphql-kotlin-hooks-provider").projectDir = file("plugins/schema/graphql-kotlin-hooks-provider")
project(":graphql-kotlin-federated-hooks-provider").projectDir = file("plugins/schema/graphql-kotlin-federated-hooks-provider")

// Servers
project(":graphql-kotlin-server").projectDir = file("servers/graphql-kotlin-server")
project(":graphql-kotlin-spring-server").projectDir = file("servers/graphql-kotlin-spring-server")
project(":graphql-kotlin-ktor-server").projectDir = file("servers/graphql-kotlin-ktor-server")

// Executions
project(":graphql-kotlin-dataloader").projectDir = file("executions/graphql-kotlin-dataloader")
project(":graphql-kotlin-dataloader-instrumentation").projectDir = file("executions/graphql-kotlin-dataloader-instrumentation")
project(":graphql-kotlin-automatic-persisted-queries").projectDir = file("executions/graphql-kotlin-automatic-persisted-queries")
