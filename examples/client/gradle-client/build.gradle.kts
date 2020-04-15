import com.expediagroup.graphql.plugin.generator.ScalarConverterMapping
// import com.expediagroup.graphql.plugin.gradle.graphql
// import com.expediagroup.graphql.plugin.gradle.tasks.DownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

plugins {
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-client")
}

// val downloadSDL by tasks.getting(DownloadSDLTask::class) {
//    endpoint.set("http://localhost:8080/sdl")
// }
val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.expediagroup.graphql.generated")
    schemaFile.set(File(project.projectDir, "schema.graphql"))
    scalarConverters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.examples.client.UUIDScalarConverter"))

//    schemaFile.set(downloadSDL.outputFile)
//    dependsOn("downloadSDL")
}

tasks {
    ktlint {
        filter {
            exclude("**/generated/**")
        }
    }
}

// graphql {
//    packageName = "com.expediagroup.graphql.generated"
//    // you can also use direct sdlEndpoint instead
//    endpoint = "http://localhost:8080/graphql"
//
//    // optional
//    allowDeprecatedFields = true
//    scalarConverters.put("UUID", ScalarConverterMapping("java.util.UUID", "com.expediagroup.graphql.examples.client.UUIDScalarConverter"))
//    queryFiles.setFrom()
// }
