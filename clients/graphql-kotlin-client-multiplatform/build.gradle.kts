description = "A lightweight typesafe GraphQL HTTP Client"

val kotlinCoroutinesVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-types-multiplatform"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
}
