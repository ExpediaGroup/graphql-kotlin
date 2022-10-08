description = "A lightweight typesafe GraphQL HTTP Client"

val kotlinxCoroutinesVersion: String by project

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
}
