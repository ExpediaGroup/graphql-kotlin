description = "A lightweight typesafe GraphQL HTTP Client"

val kotlinCoroutinesVersion: String by project

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
}
