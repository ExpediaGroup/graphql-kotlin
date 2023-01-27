import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.serialization)
    // issue https://github.com/ExpediaGroup/graphql-kotlin/issues/1625
//    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-spring-client") {
        exclude("com.expediagroup", "graphql-kotlin-client-jackson")
    }
    implementation("com.expediagroup", "graphql-kotlin-client-serialization")
    implementation("com.expediagroup", "graphql-kotlin-spring-server")
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.spring.boot.test)
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.webclient"))
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
