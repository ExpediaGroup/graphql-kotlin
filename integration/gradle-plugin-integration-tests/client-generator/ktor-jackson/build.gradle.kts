import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-ktor-client") {
        exclude("com.expediagroup", "graphql-kotlin-client-serialization")
    }
    implementation("com.expediagroup", "graphql-kotlin-client-jackson")
    implementation("com.expediagroup:graphql-kotlin-server")
    implementation(libs.icu)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.ktor.server.test.host)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.ktor.jackson"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    serializer.set(GraphQLSerializer.JACKSON)
    useOptionalInputWrapper.set(true)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
