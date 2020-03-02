import com.expediagroup.graphql.plugin.gradle.tasks.DownloadSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GenerateClientTask

plugins {
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-client")
}

val downloadSDL by tasks.getting(DownloadSDLTask::class) {
    endpoint.set("http://localhost:8080/sdl")
}
val generateClient by tasks.getting(GenerateClientTask::class) {
    packageName.set("com.expediagroup.graphql.generated")
    schemaFile.set(downloadSDL.outputFile)

    dependsOn("downloadSDL")
}

tasks {
    ktlint {
        filter {
            exclude("**/generated/**")
        }
    }
}
