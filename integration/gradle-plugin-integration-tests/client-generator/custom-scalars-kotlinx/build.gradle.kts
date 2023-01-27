import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.config.GraphQLSerializer
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-ktor-client")
    implementation("com.expediagroup:graphql-kotlin-server")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider")
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

tasks.withType<Test> {
    useJUnitPlatform()
}

val graphqlGenerateSDL by tasks.getting(com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.scalars"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.scalars.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    serializer.set(GraphQLSerializer.KOTLINX)
    useOptionalInputWrapper.set(true)

    // custom scalars
    customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.scalars.converters.UUIDScalarConverter"))
    customScalars.add(GraphQLScalar("Locale", "com.ibm.icu.util.ULocale", "com.expediagroup.scalars.converters.ULocaleScalarConverter"))
}
