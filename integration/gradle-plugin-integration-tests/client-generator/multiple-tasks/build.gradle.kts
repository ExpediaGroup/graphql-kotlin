import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.it-conventions")
    id("com.expediagroup.graphql")
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    implementation(libs.kotlin.stdlib)
}

tasks.create("generateFirst", GraphQLGenerateClientTask::class) {
    packageName.set("com.expediagroup.generated.first")
    schemaFile.set(file("${project.projectDir}/schema.graphql"))
    // optional config
    queryFiles.from(
        "${project.projectDir}/src/main/resources/queries/HelloWorldQuery.graphql"
    )
}

tasks.create("generateSecond", GraphQLGenerateClientTask::class) {
    packageName.set("com.expediagroup.generated.second")
    schemaFile.set(file("${project.projectDir}/schema.graphql"))
    // optional config
    queryFiles.from(
        "${project.projectDir}/src/main/resources/queries/UpdateNameMutation.graphql"
    )
}

tasks {
    named<Test>("test") {
        dependsOn("generateFirst", "generateSecond")

        doLast {
            // verify files were generated
            if (!File(project.buildDir, "generated/source/graphql/main/com/expediagroup/generated/first/HelloWorldQuery.kt").exists()) {
                throw RuntimeException("failed to generate client for HelloWorldQuery")
            }
            if (!File(project.buildDir, "generated/source/graphql/main/com/expediagroup/generated/second/UpdateNameMutation.kt").exists()) {
                throw RuntimeException("failed to generate client for UpdateNameMutation")
            }
        }
    }
}
