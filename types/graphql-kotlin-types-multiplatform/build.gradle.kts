description = "Core package containing the classes used for GraphQL commuication in both server and client"

plugins {
    kotlin("plugin.serialization") version "1.4.21"
}

val kotlinxSerializationVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
}
