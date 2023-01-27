import com.expediagroup.graphql.plugin.gradle.config.GraphQLScalar
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-hooks-provider")
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation("com.expediagroup:graphql-kotlin-spring-server")
    implementation(libs.icu)
    testImplementation(libs.kotlin.junit.test)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.spring.boot.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    packages.set(listOf("com.expediagroup.scalars"))
}
val graphqlGenerateTestClient by tasks.getting(GraphQLGenerateTestClientTask::class) {
    packageName.set("com.expediagroup.scalars.generated")
    schemaFile.set(graphqlGenerateSDL.schemaFile)
    useOptionalInputWrapper.set(true)

    // custom scalars
    customScalars.add(GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.scalars.converters.UUIDScalarConverter"))
    customScalars.add(GraphQLScalar("Locale", "com.ibm.icu.util.ULocale", "com.expediagroup.scalars.converters.ULocaleScalarConverter"))
}
