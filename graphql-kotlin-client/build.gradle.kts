description = "A lightweight typesafe GraphQL HTTP Client"

val ktorVersion: String by project
val kotlinCoroutinesVersion: String by project

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("io.ktor:ktor-client-cio:$ktorVersion")
    api("io.ktor:ktor-client-json:$ktorVersion")
    api("io.ktor:ktor-client-jackson:$ktorVersion")
}
