import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client")
    implementation("com.expediagroup:graphql-kotlin-server")
    implementation(libs.icu)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.ktor.server.test.host)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.ktor.kotlinx"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    serializer.set(GraphQLSerializer.KOTLINX)
    useOptionalInputWrapper.set(true)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
