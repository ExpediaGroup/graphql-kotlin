import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

val graphqlGenerateClient by tasks.getting(GraphQLGenerateClientTask::class) {
    packageName.set("com.example.generated")
    schemaFile.set(file("${project.projectDir}/schema.graphql"))
    // optional config
    allowDeprecatedFields.set(true)
    useSharedResponseTypes.set(true)
    queryFiles.from(
        "${project.projectDir}/src/main/resources/queries/HelloWorldQuery.graphql",
        "${project.projectDir}/src/main/resources/queries/UpdateNameMutation.graphql",
        "${project.projectDir}/src/main/resources/queries/FetchObjectQuery1.graphql",
        "${project.projectDir}/src/main/resources/queries/FetchObjectQuery2.graphql"
    )
}

tasks {
    named<Test>("test") {
        dependsOn("graphqlGenerateClient")

        doLast {
            val generatedDir = "generated/source/graphql/main/com/example/generated"
            // verify operation files were generated
            if (!File(project.buildDir, "$generatedDir/HelloWorldQuery.kt").exists()) {
                throw RuntimeException("failed to generate client for HelloWorldQuery")
            }
            if (!File(project.buildDir, "$generatedDir/UpdateNameMutation.kt").exists()) {
                throw RuntimeException("failed to generate client for UpdateNameMutation")
            }
            if (!File(project.buildDir, "$generatedDir/FetchObjectQuery1.kt").exists()) {
                throw RuntimeException("failed to generate client for FetchObjectQuery1")
            }
            if (!File(project.buildDir, "$generatedDir/FetchObjectQuery2.kt").exists()) {
                throw RuntimeException("failed to generate client for FetchObjectQuery2")
            }
            // verify that useSharedResponseTypes produced a shared type in the responses sub-package
            if (!File(project.buildDir, "$generatedDir/responses/ComplexObject.kt").exists()) {
                throw RuntimeException("shared response type ComplexObject was not generated — useSharedResponseTypes may not be working")
            }
        }
    }
}
