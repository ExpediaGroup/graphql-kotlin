import com.expediagroup.graphql.plugin.gradle.graphql

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/schema.graphql")
        packageName = "com.expediagroup.generated"
        // optional
        allowDeprecatedFields = true
        headers = mapOf("X-Custom-Header" to "My-Custom-Header-Value")
        queryFiles = listOf(
            file("${project.projectDir}/src/main/resources/queries/HelloWorldQuery.graphql"),
            file("${project.projectDir}/src/main/resources/queries/UpdateNameMutation.graphql")
        )
    }
}

tasks {
    named<Test>("test") {
        dependsOn("graphqlGenerateClient")

        doLast {
            // verify files were generated
            if (!File(project.buildDir, "generated/source/graphql/main/com/expediagroup/generated/HelloWorldQuery.kt").exists()) {
                throw RuntimeException("failed to generate client for HelloWorldQuery")
            }
            if (!File(project.buildDir, "generated/source/graphql/main/com/expediagroup/generated/UpdateNameMutation.kt").exists()) {
                throw RuntimeException("failed to generate client for UpdateNameMutation")
            }
        }
    }
}
